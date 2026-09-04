package com.company.erp.purchase.domain.aggregate;

import com.company.erp.purchase.domain.entity.PurchaseOrderItem;
import com.company.erp.purchase.domain.event.DomainEvent;
import com.company.erp.purchase.domain.event.PurchaseOrderReceivedEvent;
import com.company.erp.purchase.domain.exception.DomainException;
import com.company.erp.purchase.domain.valueobject.MaterialId;
import com.company.erp.purchase.domain.valueobject.Money;
import com.company.erp.purchase.domain.valueobject.OrganizationId;
import com.company.erp.purchase.domain.valueobject.PurchaseOrderId;
import com.company.erp.purchase.domain.valueobject.PurchaseOrderStatus;
import com.company.erp.purchase.domain.valueobject.Quantity;
import com.company.erp.purchase.domain.valueobject.ReceivedQuantity;
import com.company.erp.purchase.domain.valueobject.SupplierId;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PurchaseOrderTest {

    @Test
    void 创建订单应处于草稿状态并正确计算总额() {
        PurchaseOrder order = PurchaseOrder.create("PO1", new SupplierId(1L), new OrganizationId(2L),
                "CNY", List.of(item(100L, "10", "5.00"), item(200L, "2", "3.00")), "remark", 1L);

        assertThat(order.getStatus()).isEqualTo(PurchaseOrderStatus.DRAFT);
        assertThat(order.getTotalAmount().amount()).isEqualByComparingTo(new BigDecimal("56.00"));
    }

    @Test
    void 明细为空时应拒绝创建() {
        assertThatThrownBy(() -> PurchaseOrder.create("PO1", new SupplierId(1L), new OrganizationId(2L),
                "CNY", List.of(), null, 1L))
                .isInstanceOf(DomainException.class);
    }

    @Test
    void 草稿订单审核后变为已审核并产生事件() {
        PurchaseOrder order = draftOrder();

        order.review(9L);

        assertThat(order.getStatus()).isEqualTo(PurchaseOrderStatus.REVIEWED);
        assertThat(order.getEvents()).hasSize(1);
    }

    @Test
    void 已审核订单不能再次审核() {
        PurchaseOrder order = draftOrder();
        order.review(9L);

        assertThatThrownBy(() -> order.review(9L)).isInstanceOf(DomainException.class);
    }

    @Test
    void 全部入库后变为已入库() {
        PurchaseOrder order = reviewedOrder();

        order.receive(List.of(new ReceivedQuantity(new MaterialId(100L), Quantity.of(new BigDecimal("10"), "PCS"))));

        assertThat(order.getStatus()).isEqualTo(PurchaseOrderStatus.RECEIVED);
    }

    @Test
    void 部分入库后变为部分入库() {
        PurchaseOrder order = reviewedOrder();

        order.receive(List.of(new ReceivedQuantity(new MaterialId(100L), Quantity.of(new BigDecimal("5"), "PCS"))));

        assertThat(order.getStatus()).isEqualTo(PurchaseOrderStatus.PARTIAL_RECEIVED);
    }

    @Test
    void 入库数量超过采购数量应拒绝() {
        PurchaseOrder order = reviewedOrder();

        assertThatThrownBy(() -> order.receive(List.of(
                new ReceivedQuantity(new MaterialId(100L), Quantity.of(new BigDecimal("11"), "PCS")))))
                .isInstanceOf(DomainException.class);
    }

    @Test
    void 入库事件携带物料单价() {
        PurchaseOrder order = reviewedOrder();

        order.receive(List.of(new ReceivedQuantity(new MaterialId(100L), Quantity.of(new BigDecimal("10"), "PCS"))));

        DomainEvent event = order.getEvents().get(0);
        assertThat(event).isInstanceOf(PurchaseOrderReceivedEvent.class);
        PurchaseOrderReceivedEvent received = (PurchaseOrderReceivedEvent) event;
        assertThat(received.getItems()).hasSize(1);
        assertThat(received.getItems().get(0).unitPrice().amount()).isEqualByComparingTo(new BigDecimal("5.00"));
    }

    @Test
    void 已审核订单可以关闭() {
        PurchaseOrder order = reviewedOrder();

        order.close(9L, "供应商取消");

        assertThat(order.getStatus()).isEqualTo(PurchaseOrderStatus.CLOSED);
    }

    private PurchaseOrderItem item(long materialId, String qty, String price) {
        return new PurchaseOrderItem(new MaterialId(materialId),
                Quantity.of(new BigDecimal(qty), "PCS"),
                Money.of(new BigDecimal(price), "CNY"));
    }

    private PurchaseOrder draftOrder() {
        PurchaseOrder order = PurchaseOrder.create("PO1", new SupplierId(1L), new OrganizationId(2L),
                "CNY", List.of(item(100L, "10", "5.00")), null, 1L);
        order.assignId(new PurchaseOrderId(1L));
        return order;
    }

    private PurchaseOrder reviewedOrder() {
        PurchaseOrder order = draftOrder();
        order.review(9L);
        order.clearEvents();
        return order;
    }
}