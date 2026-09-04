package com.company.erp.purchase.domain.aggregate;

import com.company.erp.purchase.domain.entity.PurchaseOrderItem;
import com.company.erp.purchase.domain.exception.PurchaseDomainException;
import com.company.erp.purchase.domain.valueobject.MaterialId;
import com.company.erp.purchase.domain.valueobject.Money;
import com.company.erp.purchase.domain.valueobject.PurchaseOrderStatus;
import com.company.erp.purchase.domain.valueobject.Quantity;
import com.company.erp.purchase.domain.valueobject.ReceivedQuantity;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SuppressWarnings("ConstantConditions")
class PurchaseOrderTest {

    private static final Long SUPPLIER_ID = 10L;
    private static final Long ORG_ID = 1L;
    private static final MaterialId MATERIAL_ID = new MaterialId(100L);

    private PurchaseOrder newDraftOrder() {
        return PurchaseOrder.create("PO-001", SUPPLIER_ID, ORG_ID, List.of(newItem()));
    }

    private PurchaseOrderItem newItem() {
        return PurchaseOrderItem.create(MATERIAL_ID,
                new Quantity(new BigDecimal("10"), "PCS"),
                new Money(new BigDecimal("5.00"), "CNY"));
    }

    @Test
    void shouldCreateAsDraftWithoutId() {
        PurchaseOrder order = newDraftOrder();

        assertThat(order.getStatus()).isEqualTo(PurchaseOrderStatus.DRAFT);
        assertThat(order.getId()).isNull();
        assertThat(order.getItems()).hasSize(1);
        assertThat(order.getOrderCode()).isEqualTo("PO-001");
    }

    @Test
    void shouldRejectBlankOrderCode() {
        assertThatThrownBy(() -> PurchaseOrder.create(" ", SUPPLIER_ID, ORG_ID, List.of(newItem())))
                .isInstanceOf(PurchaseDomainException.class)
                .hasMessageContaining("订单编号");
    }

    @Test
    void shouldRejectInvalidSupplierId() {
        assertThatThrownBy(() -> PurchaseOrder.create("PO-002", null, ORG_ID, List.of(newItem())))
                .isInstanceOf(PurchaseDomainException.class);
    }

    @Test
    void shouldRejectInvalidOrgId() {
        assertThatThrownBy(() -> PurchaseOrder.create("PO-003", SUPPLIER_ID, 0L, List.of(newItem())))
                .isInstanceOf(PurchaseDomainException.class);
    }

    @Test
    void shouldRejectMissingItems() {
        assertThatThrownBy(() -> PurchaseOrder.create("PO-004", SUPPLIER_ID, ORG_ID, List.of()))
                .isInstanceOf(PurchaseDomainException.class)
                .hasMessageContaining("明细");
    }

    @Test
    void shouldReviewDraftOrder() {
        PurchaseOrder order = newDraftOrder();

        order.review(9L);

        assertThat(order.getStatus()).isEqualTo(PurchaseOrderStatus.REVIEWED);
    }

    @Test
    void shouldRejectNullReviewer() {
        PurchaseOrder order = newDraftOrder();

        assertThatThrownBy(() -> order.review(null))
                .isInstanceOf(PurchaseDomainException.class);
    }

    @Test
    void shouldRejectReviewingReviewedOrder() {
        PurchaseOrder order = newDraftOrder();
        order.review(9L);

        assertThatThrownBy(() -> order.review(9L))
                .isInstanceOf(PurchaseDomainException.class)
                .hasMessageContaining("草稿");
    }

    @Test
    void shouldRejectReceivingDraftOrder() {
        PurchaseOrder order = newDraftOrder();

        assertThatThrownBy(() -> order.receive(List.of(received(5))))
                .isInstanceOf(PurchaseDomainException.class)
                .hasMessageContaining("已审核");
    }

    @Test
    void shouldRejectEmptyReceivedItems() {
        PurchaseOrder order = newDraftOrder();
        order.review(9L);

        assertThatThrownBy(() -> order.receive(List.of()))
                .isInstanceOf(PurchaseDomainException.class);
    }

    @Test
    void shouldKeepReviewedStatusOnPartialReceive() {
        PurchaseOrder order = newDraftOrder();
        order.review(9L);

        order.receive(List.of(received(5)));

        assertThat(order.getStatus()).isEqualTo(PurchaseOrderStatus.REVIEWED);
        assertThat(order.getItems().get(0).getReceivedQuantity().value())
                .isEqualByComparingTo(new BigDecimal("5"));
    }

    @Test
    void shouldBecomeReceivedWhenFullyReceived() {
        PurchaseOrder order = newDraftOrder();
        order.review(9L);

        order.receive(List.of(received(10)));

        assertThat(order.getStatus()).isEqualTo(PurchaseOrderStatus.RECEIVED);
        assertThat(order.isFullyReceived()).isTrue();
    }

    @Test
    void shouldRejectUnknownMaterial() {
        PurchaseOrder order = newDraftOrder();
        order.review(9L);

        assertThatThrownBy(() -> order.receive(List.of(
                new ReceivedQuantity(new MaterialId(999L), new Quantity(new BigDecimal("1"), "PCS")))))
                .isInstanceOf(PurchaseDomainException.class)
                .hasMessageContaining("不在采购订单");
    }

    @Test
    void shouldRejectOverReceiving() {
        PurchaseOrder order = newDraftOrder();
        order.review(9L);

        assertThatThrownBy(() -> order.receive(List.of(received(11))))
                .isInstanceOf(PurchaseDomainException.class)
                .hasMessageContaining("超过");
    }

    @Test
    void shouldCloseReviewedOrder() {
        PurchaseOrder order = newDraftOrder();
        order.review(9L);

        order.close();

        assertThat(order.getStatus()).isEqualTo(PurchaseOrderStatus.CLOSED);
    }

    @Test
    void shouldRejectClosingReceivedOrder() {
        PurchaseOrder order = newDraftOrder();
        order.review(9L);
        order.receive(List.of(received(10)));

        assertThatThrownBy(order::close)
                .isInstanceOf(PurchaseDomainException.class)
                .hasMessageContaining("不能关闭");
    }

    @Test
    void shouldReconstituteReviewedOrder() {
        PurchaseOrder order = newDraftOrder();
        order.review(9L);

        PurchaseOrder reloaded = PurchaseOrder.reconstitute(order.getId(), order.getOrderCode(),
                order.getSupplierId(), order.getOrgId(), order.getStatus(), List.of(newItem()),
                order.getCreatedAt(), order.getUpdatedAt());

        assertThat(reloaded.getStatus()).isEqualTo(PurchaseOrderStatus.REVIEWED);
    }

    private ReceivedQuantity received(int quantity) {
        return new ReceivedQuantity(MATERIAL_ID, new Quantity(new BigDecimal(quantity), "PCS"));
    }
}