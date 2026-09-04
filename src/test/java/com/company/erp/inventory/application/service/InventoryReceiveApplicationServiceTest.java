package com.company.erp.inventory.application.service;

import com.company.erp.inventory.application.command.ReceiveInventoryCommand;
import com.company.erp.inventory.application.port.InventoryDistributedLock;
import com.company.erp.inventory.application.port.InventoryEventIdempotencyStore;
import com.company.erp.inventory.domain.aggregate.InventoryBalance;
import com.company.erp.inventory.domain.exception.InventoryDomainException;
import com.company.erp.inventory.domain.repository.InventoryBalanceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InventoryReceiveApplicationServiceTest {

    @Mock
    private InventoryBalanceRepository repository;

    @Mock
    private InventoryEventIdempotencyStore idempotencyStore;

    @Mock
    private InventoryDistributedLock lock;

    @InjectMocks
    private InventoryReceiveApplicationService service;

    @Test
    void 已处理事件直接跳过不加锁也不入库() {
        when(idempotencyStore.markIfAbsent("evt")).thenReturn(false);

        service.receive(List.of(cmd("evt", 1L, 100L)));

        verify(lock, never()).execute(anyString(), any());
        verify(repository, never()).save(any());
    }

    @Test
    void 正常入库应在锁内保存结存() {
        when(idempotencyStore.markIfAbsent("evt")).thenReturn(true);
        when(repository.findForUpdateByMaterialAndOrg(100L, 1L)).thenReturn(Optional.empty());
        doAnswer(inv -> {
            ((Runnable) inv.getArgument(1)).run();
            return null;
        }).when(lock).execute(anyString(), any());

        service.receive(List.of(cmd("evt", 1L, 100L)));

        verify(lock).execute(eq("inventory:material_1_100"), any());
        verify(repository).save(any(InventoryBalance.class));
    }

    @Test
    void 获取锁失败应回滚幂等标记并抛出异常() {
        when(idempotencyStore.markIfAbsent("evt")).thenReturn(true);
        doThrow(new InventoryDomainException("库存操作繁忙，请稍后重试"))
                .when(lock).execute(anyString(), any());

        assertThatThrownBy(() -> service.receive(List.of(cmd("evt", 1L, 100L))))
                .isInstanceOf(InventoryDomainException.class);

        verify(idempotencyStore).remove("evt");
    }

    private ReceiveInventoryCommand cmd(String eventId, long orgId, long materialId) {
        return new ReceiveInventoryCommand(eventId, 1L, materialId, orgId,
                new BigDecimal("10"), "PCS", new BigDecimal("5.00"), "CNY");
    }
}