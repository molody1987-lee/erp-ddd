package com.company.erp.inventory.application.service;

import com.company.erp.inventory.application.acl.InventoryReceiveItem;
import com.company.erp.inventory.application.acl.PurchaseOrderReceivedEventTranslator;
import com.company.erp.inventory.domain.aggregate.InventoryBalance;
import com.company.erp.inventory.domain.exception.InventoryDomainException;
import com.company.erp.inventory.domain.repository.InventoryBalanceRepository;
import com.company.erp.inventory.domain.service.InventoryDistributedLock;
import com.company.erp.inventory.domain.service.InventoryEventIdempotencyStore;
import com.company.erp.inventory.domain.valueobject.InventoryQuantity;
import com.company.erp.purchase.domain.event.PurchaseOrderReceivedEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InventoryAppServiceTest {

    @Mock
    private InventoryBalanceRepository repository;
    @Mock
    private InventoryEventIdempotencyStore idempotencyStore;
    @Mock
    private InventoryDistributedLock distributedLock;
    @Mock
    private PurchaseOrderReceivedEventTranslator translator;
    @InjectMocks
    private InventoryAppService appService;

    private PurchaseOrderReceivedEvent event() {
        return new PurchaseOrderReceivedEvent("evt-1", 1L, 200L,
                List.of(new PurchaseOrderReceivedEvent.Item(100L, new BigDecimal("10"), "PCS", new BigDecimal("5.00"))),
                LocalDateTime.now());
    }

    @Test
    void shouldSkipDuplicateEvent() {
        when(idempotencyStore.markIfAbsent("evt-1")).thenReturn(false);

        appService.handlePurchaseReceived(event());

        verify(distributedLock, never()).execute(anyString(), any(Runnable.class));
        verify(repository, never()).save(any());
    }

    @Test
    void shouldCreateBalanceOnFirstReceive() {
        when(idempotencyStore.markIfAbsent("evt-1")).thenReturn(true);
        when(translator.translate(any())).thenReturn(List.of(
                new InventoryReceiveItem(100L, InventoryQuantity.of(new BigDecimal("10"), "PCS"),
                        new BigDecimal("5.00"))));
        when(repository.findForUpdateByMaterialAndOrg(100L, 1L)).thenReturn(Optional.empty());
        doAnswer(invocation -> {
            ((Runnable) invocation.getArgument(1)).run();
            return null;
        }).when(distributedLock).execute(anyString(), any(Runnable.class));

        appService.handlePurchaseReceived(event());

        verify(repository).save(any(InventoryBalance.class));
        verify(idempotencyStore, never()).remove("evt-1");
    }

    @Test
    void shouldRollbackIdempotencyMarkOnLockConflict() {
        when(idempotencyStore.markIfAbsent("evt-1")).thenReturn(true);
        when(translator.translate(any())).thenReturn(List.of(
                new InventoryReceiveItem(100L, InventoryQuantity.of(new BigDecimal("10"), "PCS"),
                        new BigDecimal("5.00"))));
        doThrow(new InventoryDomainException("获取分布式锁失败"))
                .when(distributedLock).execute(anyString(), any(Runnable.class));

        assertThatThrownBy(() -> appService.handlePurchaseReceived(event()))
                .isInstanceOf(InventoryDomainException.class);
        verify(idempotencyStore).remove("evt-1");
        assertThat(true).isTrue();
    }
}