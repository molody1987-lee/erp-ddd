package com.company.erp.purchase.application.service;

import com.company.erp.purchase.application.command.CreatePurchaseOrderCommand;
import com.company.erp.purchase.application.command.ItemCommand;
import com.company.erp.purchase.application.port.DomainEventPublisher;
import com.company.erp.purchase.application.port.OrderCodeGenerator;
import com.company.erp.purchase.domain.aggregate.PurchaseOrder;
import com.company.erp.purchase.domain.event.PurchaseOrderCreatedEvent;
import com.company.erp.purchase.domain.repository.PurchaseOrderRepository;
import com.company.erp.purchase.domain.valueobject.PurchaseOrderId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PurchaseOrderApplicationServiceTest {

    @Mock
    private PurchaseOrderRepository repository;

    @Mock
    private DomainEventPublisher publisher;

    @Mock
    private OrderCodeGenerator orderCodeGenerator;

    @InjectMocks
    private PurchaseOrderApplicationService service;

    @Test
    void 创建订单应保存聚合并返回ID() {
        when(orderCodeGenerator.nextCode()).thenReturn("PO20260904000001");
        when(repository.save(any(PurchaseOrder.class))).thenAnswer(invocation -> {
            PurchaseOrder order = invocation.getArgument(0);
            order.assignId(new PurchaseOrderId(100L));
            return order.getId();
        });

        Long orderId = service.create(new CreatePurchaseOrderCommand(1L, 2L, "CNY", null, 1L,
                List.of(new ItemCommand(100L, new BigDecimal("10"), "PCS", new BigDecimal("5.00")))));

        assertThat(orderId).isEqualTo(100L);
        verify(publisher).publish(any(PurchaseOrderCreatedEvent.class));
    }
}