package com.company.erp.purchase.domain.valueobject;

import com.company.erp.purchase.domain.exception.PurchaseDomainException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SuppressWarnings("ConstantConditions")
class PurchaseOrderValueObjectTest {

    @Test
    void shouldRejectNullOrNegativeQuantity() {
        assertThatThrownBy(() -> new Quantity(null, "PCS")).isInstanceOf(PurchaseDomainException.class);
        assertThatThrownBy(() -> new Quantity(new BigDecimal("-1"), "PCS"))
                .isInstanceOf(PurchaseDomainException.class);
        assertThatThrownBy(() -> new Quantity(BigDecimal.ONE, " "))
                .isInstanceOf(PurchaseDomainException.class);
    }

    @Test
    void shouldAddSameUnitAndRejectDifferentUnit() {
        Quantity q1 = new Quantity(new BigDecimal("3"), "PCS");
        Quantity q2 = new Quantity(new BigDecimal("4"), "PCS");

        assertThat(q1.add(q2).value()).isEqualByComparingTo(new BigDecimal("7"));
        assertThatThrownBy(() -> q1.add(new Quantity(BigDecimal.ONE, "KG")))
                .isInstanceOf(PurchaseDomainException.class);
    }

    @Test
    void shouldValidateAndMultiplyMoney() {
        assertThatThrownBy(() -> new Money(null, "CNY")).isInstanceOf(PurchaseDomainException.class);
        assertThatThrownBy(() -> new Money(BigDecimal.ONE, " ")).isInstanceOf(PurchaseDomainException.class);

        Money total = new Money(new BigDecimal("5.00"), "CNY")
                .multiply(new Quantity(new BigDecimal("3"), "PCS"));
        assertThat(total.amount()).isEqualByComparingTo(new BigDecimal("15.00"));
    }

    @Test
    void shouldValidateMaterialAndOrderIds() {
        assertThatThrownBy(() -> new MaterialId(null)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new MaterialId(0L)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new PurchaseOrderId(null)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new PurchaseOrderId(-1L)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldEqualForSameValueObject() {
        assertThat(new MaterialId(1L)).isEqualTo(new MaterialId(1L));
        assertThat(new PurchaseOrderId(2L)).isEqualTo(new PurchaseOrderId(2L));
        assertThat(new Quantity(BigDecimal.ONE, "PCS")).isEqualTo(new Quantity(BigDecimal.ONE, "PCS"));
        assertThat(new Money(BigDecimal.ONE, "CNY")).isEqualTo(new Money(BigDecimal.ONE, "CNY"));
    }

    @Test
    void shouldValidateReceivedQuantity() {
        assertThatThrownBy(() -> new ReceivedQuantity(null, new Quantity(BigDecimal.ONE, "PCS")))
                .isInstanceOf(PurchaseDomainException.class);
        assertThatThrownBy(() -> new ReceivedQuantity(new MaterialId(1L), null))
                .isInstanceOf(PurchaseDomainException.class);
        assertThatThrownBy(() -> new ReceivedQuantity(new MaterialId(1L),
                new Quantity(BigDecimal.ZERO, "PCS")))
                .isInstanceOf(PurchaseDomainException.class);
    }

    @Test
    void shouldHonorHashCodeAndCrossTypeEquality() {
        MaterialId material = new MaterialId(1L);
        assertThat(material).isEqualTo(material).isNotEqualTo(new Object());
        assertThat(material.hashCode()).isEqualTo(new MaterialId(1L).hashCode());

        PurchaseOrderId orderId = new PurchaseOrderId(2L);
        assertThat(orderId).isEqualTo(orderId).isNotEqualTo("other");
        assertThat(orderId.hashCode()).isEqualTo(new PurchaseOrderId(2L).hashCode());

        Quantity quantity = new Quantity(BigDecimal.ONE, "PCS");
        assertThat(quantity).isEqualTo(quantity).isNotEqualTo(new Object());
        assertThat(quantity.hashCode()).isEqualTo(new Quantity(BigDecimal.ONE, "PCS").hashCode());

        Money money = new Money(BigDecimal.ONE, "CNY");
        assertThat(money).isEqualTo(money).isNotEqualTo(new Object());
        assertThat(money.hashCode()).isEqualTo(new Money(BigDecimal.ONE, "CNY").hashCode());
    }

    @Test
    void shouldConvertStatusCode() {
        assertThat(PurchaseOrderStatus.fromCode(0)).isEqualTo(PurchaseOrderStatus.DRAFT);
        assertThat(PurchaseOrderStatus.fromCode(1)).isEqualTo(PurchaseOrderStatus.REVIEWED);
        assertThat(PurchaseOrderStatus.fromCode(2)).isEqualTo(PurchaseOrderStatus.RECEIVED);
        assertThat(PurchaseOrderStatus.fromCode(3)).isEqualTo(PurchaseOrderStatus.CLOSED);
        assertThat(PurchaseOrderStatus.RECEIVED.getCode()).isEqualTo(2);

        assertThatThrownBy(() -> PurchaseOrderStatus.fromCode(99))
                .isInstanceOf(IllegalArgumentException.class);
    }
}