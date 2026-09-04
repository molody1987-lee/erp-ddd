package com.company.erp.purchase.application.service;

import com.company.erp.purchase.application.command.CreatePurchaseOrderCommand;
import com.company.erp.purchase.application.command.CreatePurchaseOrderItemCommand;
import com.company.erp.purchase.application.command.ReceivePurchaseOrderCommand;
import com.company.erp.purchase.application.command.ReviewPurchaseOrderCommand;
import com.company.erp.purchase.domain.aggregate.PurchaseOrder;
import com.company.erp.purchase.domain.entity.PurchaseOrderItem;
import com.company.erp.purchase.domain.event.PurchaseOrderEventPublisher;
import com.company.erp.purchase.domain.event.PurchaseOrderReceivedEvent;
import com.company.erp.purchase.domain.exception.PurchaseDomainException;
import com.company.erp.purchase.domain.repository.PurchaseOrderRepository;
import com.company.erp.purchase.domain.valueobject.MaterialId;
import com.company.erp.purchase.domain.valueobject.Money;
import com.company.erp.purchase.domain.valueobject.PurchaseOrderId;
import com.company.erp.purchase.domain.valueobject.PurchaseOrderStatus;
import com.company.erp.purchase.domain.valueobject.Quantity;
import com.company.erp.purchase.domain.valueobject.ReceivedQuantity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PurchaseOrderAppServiceTest {

    @Mock
    private PurchaseOrderRepository repository;
    @Mock
    private PurchaseOrderEventPublisher eventPublisher;
    @InjectMocks
    private PurchaseOrderAppService appService;

    private final MaterialId materialId = new MaterialId(100L);

    @Test
    void shouldCreateOrderAndReturnId() {
        when(repository.save(any(PurchaseOrder.class))).thenReturn(new PurchaseOrderId(1L));

        PurchaseOrderId id = appService.createOrder(new CreatePurchaseOrderCommand(
                "PO-100", 10L, 1L, List.of(
                new CreatePurchaseOrderItemCommand(100L, new BigDecimal("10"), "PCS",
                        new BigDecimal("5.00"), "CNY"))));

        assertThat(id.value()).isEqualTo(1L);
        verify(repository).save(any(PurchaseOrder.class));
    }

    @Test
    void shouldReviewOrderAndSave() {
        PurchaseOrder order = orderWithItem();
        when(repository.findById(new PurchaseOrderId(1L))).thenReturn(Optional.of(order));

        appService.reviewOrder(new ReviewPurchaseOrderCommand(new PurchaseOrderId(1L), 9L));

        assertThat(order.getStatus()).isEqualTo(PurchaseOrderStatus.REVIEWED);
        verify(repository).save(order);
    }

    @Test
    void shouldThrowWhenOrderNotFound() {
        when(repository.findById(new PurchaseOrderId(2L))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> appService.reviewOrder(
                new ReviewPurchaseOrderCommand(new PurchaseOrderId(2L), 9L)))
                .isInstanceOf(PurchaseDomainException.class)
                .hasMessageContaining("不存在");
    }

    @Test
    void shouldReceiveOrderAndSave() {
        PurchaseOrder order = orderWithItem();
        order.assignId(new PurchaseOrderId(1L));
        order.review(9L);
        when(repository.findById(new PurchaseOrderId(1L))).thenReturn(Optional.of(order));

        appService.receiveOrder(new ReceivePurchaseOrderCommand(
                new PurchaseOrderId(1L),
                List.of(new ReceivedQuantity(materialId, new Quantity(new BigDecimal("10"), "PCS")))));

        assertThat(order.getStatus()).isEqualTo(PurchaseOrderStatus.RECEIVED);
        verify(repository).save(order);
        verify(eventPublisher).publish(any(PurchaseOrderReceivedEvent.class));
    }

    private PurchaseOrder orderWithItem() {
        PurchaseOrderItem item = PurchaseOrderItem.create(materialId,
                new Quantity(new BigDecimal("10"), "PCS"),
                new Money(new BigDecimal("5.00"), "CNY"));
        return PurchaseOrder.create("PO-1", 10L, 1L, List.of(item));
    }
}