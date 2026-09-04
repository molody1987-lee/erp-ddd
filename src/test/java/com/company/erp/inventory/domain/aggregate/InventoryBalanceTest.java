package com.company.erp.inventory.domain.aggregate;

import com.company.erp.inventory.domain.exception.InventoryDomainException;
import com.company.erp.inventory.domain.valueobject.InventoryQuantity;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SuppressWarnings("ConstantConditions")
class InventoryBalanceTest {

    @Test
    void shouldCreateWithZeroQuantityAndCost() {
        InventoryBalance balance = InventoryBalance.create(100L, 1L, "PCS");

        assertThat(balance.getQuantityOnHand()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(balance.getUnitCost()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(balance.getMaterialId()).isEqualTo(100L);
        assertThat(balance.getOrgId()).isEqualTo(1L);
        assertThat(balance.getId()).isNull();
    }

    @Test
    void shouldRejectInvalidMaterialId() {
        assertThatThrownBy(() -> InventoryBalance.create(null, 1L, "PCS"))
                .isInstanceOf(InventoryDomainException.class);
    }

    @Test
    void shouldRejectInvalidOrgId() {
        assertThatThrownBy(() -> InventoryBalance.create(100L, 0L, "PCS"))
                .isInstanceOf(InventoryDomainException.class);
    }

    @Test
    void shouldRejectBlankUnit() {
        assertThatThrownBy(() -> InventoryBalance.create(100L, 1L, " "))
                .isInstanceOf(InventoryDomainException.class);
    }

    @Test
    void shouldReceiveFirstInboundCorrectly() {
        InventoryBalance balance = InventoryBalance.create(100L, 1L, "PCS");

        balance.receive(InventoryQuantity.of(new BigDecimal("10"), "PCS"), new BigDecimal("5.00"));

        assertThat(balance.getQuantityOnHand()).isEqualByComparingTo(new BigDecimal("10"));
        assertThat(balance.getUnitCost()).isEqualByComparingTo(new BigDecimal("5.0000"));
    }

    @Test
    void shouldRecalculateMovingAverageCost() {
        InventoryBalance balance = InventoryBalance.create(100L, 1L, "PCS");
        balance.receive(InventoryQuantity.of(new BigDecimal("10"), "PCS"), new BigDecimal("5.00"));

        balance.receive(InventoryQuantity.of(new BigDecimal("10"), "PCS"), new BigDecimal("15.00"));

        assertThat(balance.getQuantityOnHand()).isEqualByComparingTo(new BigDecimal("20"));
        assertThat(balance.getUnitCost()).isEqualByComparingTo(new BigDecimal("10.0000"));
    }

    @Test
    void shouldRejectNonPositiveInboundQuantity() {
        InventoryBalance balance = InventoryBalance.create(100L, 1L, "PCS");

        assertThatThrownBy(() -> balance.receive(InventoryQuantity.of(BigDecimal.ZERO, "PCS"), BigDecimal.ONE))
                .isInstanceOf(InventoryDomainException.class);
    }

    @Test
    void shouldRejectNegativeInboundUnitCost() {
        InventoryBalance balance = InventoryBalance.create(100L, 1L, "PCS");

        assertThatThrownBy(() -> balance.receive(
                InventoryQuantity.of(BigDecimal.ONE, "PCS"), new BigDecimal("-1")))
                .isInstanceOf(InventoryDomainException.class);
    }

    @Test
    void shouldRejectMismatchedInboundUnit() {
        InventoryBalance balance = InventoryBalance.create(100L, 1L, "PCS");

        assertThatThrownBy(() -> balance.receive(
                InventoryQuantity.of(BigDecimal.ONE, "KG"), BigDecimal.ONE))
                .isInstanceOf(InventoryDomainException.class)
                .hasMessageContaining("单位");
    }

    @Test
    void shouldReconstituteBalance() {
        InventoryBalance reloaded = InventoryBalance.reconstitute(9L, 100L, 1L,
                InventoryQuantity.of(new BigDecimal("10"), "PCS"),
                new BigDecimal("5.00"), null, null);

        assertThat(reloaded.getId()).isEqualTo(9L);
        assertThat(reloaded.getQuantityOnHand()).isEqualByComparingTo(new BigDecimal("10"));
        assertThat(reloaded.getUnitCost()).isEqualByComparingTo(new BigDecimal("5.00"));
    }

    @Test
    void shouldReadIdAfterAssign() {
        InventoryBalance balance = InventoryBalance.create(100L, 1L, "PCS");

        balance.assignId(42L);

        assertThat(balance.getId()).isEqualTo(42L);
    }
}