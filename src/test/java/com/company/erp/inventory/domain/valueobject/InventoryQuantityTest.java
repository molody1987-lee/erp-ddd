package com.company.erp.inventory.domain.valueobject;

import com.company.erp.inventory.domain.exception.InventoryDomainException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SuppressWarnings("ConstantConditions")
class InventoryQuantityTest {

    @Test
    void shouldRejectNullOrNegativeValue() {
        assertThatThrownBy(() -> new InventoryQuantity(null, "PCS"))
                .isInstanceOf(InventoryDomainException.class);
        assertThatThrownBy(() -> new InventoryQuantity(new BigDecimal("-1"), "PCS"))
                .isInstanceOf(InventoryDomainException.class);
    }

    @Test
    void shouldRejectBlankUnit() {
        assertThatThrownBy(() -> new InventoryQuantity(BigDecimal.ONE, " "))
                .isInstanceOf(InventoryDomainException.class);
    }

    @Test
    void shouldCreateViaFactoryAndReadValues() {
        InventoryQuantity quantity = InventoryQuantity.of(new BigDecimal("12.5"), "PCS");

        assertThat(quantity.value()).isEqualByComparingTo(new BigDecimal("12.5"));
        assertThat(quantity.unit()).isEqualTo("PCS");
    }

    @Test
    void shouldEqualForSameValue() {
        assertThat(InventoryQuantity.of(new BigDecimal("1"), "PCS"))
                .isEqualTo(InventoryQuantity.of(new BigDecimal("1"), "PCS"));
    }
}