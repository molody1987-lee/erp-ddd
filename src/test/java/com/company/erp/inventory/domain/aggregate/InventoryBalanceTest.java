package com.company.erp.inventory.domain.aggregate;

import com.company.erp.inventory.domain.exception.InventoryDomainException;
import com.company.erp.inventory.domain.valueobject.InventoryQuantity;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InventoryBalanceTest {

    @Test
    void 首次入库应建立结存并计算平均成本() {
        InventoryBalance balance = InventoryBalance.create(100L, 1L, "PCS", "CNY");

        balance.receive(InventoryQuantity.of(new BigDecimal("10"), "PCS"), new BigDecimal("5.00"));

        assertThat(balance.getQuantityOnHand()).isEqualByComparingTo(new BigDecimal("10.00"));
        assertThat(balance.getTotalCost()).isEqualByComparingTo(new BigDecimal("50.00"));
        assertThat(balance.averageCost()).isEqualByComparingTo(new BigDecimal("5.0000"));
    }

    @Test
    void 再次入库应重算移动加权平均成本() {
        InventoryBalance balance = InventoryBalance.create(100L, 1L, "PCS", "CNY");
        balance.receive(InventoryQuantity.of(new BigDecimal("10"), "PCS"), new BigDecimal("5.00"));

        balance.receive(InventoryQuantity.of(new BigDecimal("10"), "PCS"), new BigDecimal("10.00"));

        assertThat(balance.getQuantityOnHand()).isEqualByComparingTo(new BigDecimal("20.00"));
        assertThat(balance.getTotalCost()).isEqualByComparingTo(new BigDecimal("150.00"));
        assertThat(balance.averageCost()).isEqualByComparingTo(new BigDecimal("7.5000"));
    }

    @Test
    void 无结存时平均成本为零() {
        InventoryBalance balance = InventoryBalance.create(100L, 1L, "PCS", "CNY");

        assertThat(balance.averageCost()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void 入库数量非正数应拒绝() {
        InventoryBalance balance = InventoryBalance.create(100L, 1L, "PCS", "CNY");

        assertThatThrownBy(() -> balance.receive(
                InventoryQuantity.of(new BigDecimal("0"), "PCS"), new BigDecimal("5.00")))
                .isInstanceOf(InventoryDomainException.class);
    }

    @Test
    void 单位不一致应拒绝() {
        InventoryBalance balance = InventoryBalance.create(100L, 1L, "PCS", "CNY");

        assertThatThrownBy(() -> balance.receive(
                InventoryQuantity.of(new BigDecimal("1"), "BOX"), new BigDecimal("5.00")))
                .isInstanceOf(InventoryDomainException.class);
    }
}