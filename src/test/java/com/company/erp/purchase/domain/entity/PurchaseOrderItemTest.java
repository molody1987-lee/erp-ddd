package com.company.erp.purchase.domain.entity;

import com.company.erp.purchase.domain.exception.PurchaseDomainException;
import com.company.erp.purchase.domain.valueobject.MaterialId;
import com.company.erp.purchase.domain.valueobject.Money;
import com.company.erp.purchase.domain.valueobject.Quantity;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SuppressWarnings("ConstantConditions")
class PurchaseOrderItemTest {

    private final MaterialId materialId = new MaterialId(100L);
    private final Quantity quantity = new Quantity(new BigDecimal("10"), "PCS");
    private final Money unitPrice = new Money(new BigDecimal("5.00"), "CNY");

    @Test
    void shouldCreateWithZeroReceivedQuantity() {
        PurchaseOrderItem item = PurchaseOrderItem.create(materialId, quantity, unitPrice);

        assertThat(item.getReceivedQuantity().value()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(item.getId()).isNull();
    }

    @Test
    void shouldRejectNullMaterial() {
        assertThatThrownBy(() -> PurchaseOrderItem.create(null, quantity, unitPrice))
                .isInstanceOf(PurchaseDomainException.class);
    }

    @Test
    void shouldRejectNonPositiveQuantity() {
        assertThatThrownBy(() -> PurchaseOrderItem.create(materialId,
                new Quantity(BigDecimal.ZERO, "PCS"), unitPrice))
                .isInstanceOf(PurchaseDomainException.class);
    }

    @Test
    void shouldRejectNegativeUnitPrice() {
        assertThatThrownBy(() -> PurchaseOrderItem.create(materialId, quantity,
                new Money(new BigDecimal("-1"), "CNY")))
                .isInstanceOf(PurchaseDomainException.class);
    }

    @Test
    void shouldAccumulateReceivedAndComputeAmount() {
        PurchaseOrderItem item = PurchaseOrderItem.create(materialId, quantity, unitPrice);

        item.receive(new Quantity(new BigDecimal("4"), "PCS"));

        assertThat(item.getReceivedQuantity().value()).isEqualByComparingTo(new BigDecimal("4"));
        assertThat(item.amount().amount()).isEqualByComparingTo(new BigDecimal("50.0000"));
    }

    @Test
    void shouldRejectMismatchedUnit() {
        PurchaseOrderItem item = PurchaseOrderItem.create(materialId, quantity, unitPrice);

        assertThatThrownBy(() -> item.receive(new Quantity(new BigDecimal("1"), "KG")))
                .isInstanceOf(PurchaseDomainException.class);
    }

    @Test
    void shouldRejectOverReceiving() {
        PurchaseOrderItem item = PurchaseOrderItem.create(materialId, quantity, unitPrice);

        assertThatThrownBy(() -> item.receive(new Quantity(new BigDecimal("11"), "PCS")))
                .isInstanceOf(PurchaseDomainException.class)
                .hasMessageContaining("超过");
    }

    @Test
    void shouldRejectNullReceivedQuantity() {
        PurchaseOrderItem item = PurchaseOrderItem.create(materialId, quantity, unitPrice);

        assertThatThrownBy(() -> item.receive(null))
                .isInstanceOf(PurchaseDomainException.class);
    }
}