package com.company.erp.inventory.infrastructure.repository;

import com.company.erp.inventory.domain.aggregate.InventoryBalance;
import com.company.erp.inventory.domain.valueobject.InventoryQuantity;
import com.company.erp.inventory.infrastructure.persistence.InventoryBalancePO;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class InventoryBalanceConverterTest {

    private final InventoryBalanceConverter converter = new InventoryBalanceConverter();

    @Test
    void shouldConvertBalanceToPoPreservingFields() {
        InventoryBalance balance = InventoryBalance.create(100L, 1L, "PCS");
        balance.receive(InventoryQuantity.of(new BigDecimal("10"), "PCS"), new BigDecimal("5.00"));
        balance.assignId(7L);

        InventoryBalancePO po = converter.toPO(balance);

        assertThat(po.getId()).isEqualTo(7L);
        assertThat(po.getMaterialId()).isEqualTo(100L);
        assertThat(po.getOrgId()).isEqualTo(1L);
        assertThat(po.getQuantityOnHand()).isEqualByComparingTo(new BigDecimal("10"));
        assertThat(po.getUnit()).isEqualTo("PCS");
        assertThat(po.getUnitCost()).isEqualByComparingTo(new BigDecimal("5.0000"));
    }

    @Test
    void shouldConvertPoToDomainBalance() {
        InventoryBalancePO po = new InventoryBalancePO();
        po.setId(7L);
        po.setMaterialId(100L);
        po.setOrgId(1L);
        po.setQuantityOnHand(new BigDecimal("10"));
        po.setUnit("PCS");
        po.setUnitCost(new BigDecimal("5.00"));
        po.setCreateTime(LocalDateTime.now());
        po.setUpdateTime(LocalDateTime.now());

        InventoryBalance balance = converter.toDomain(po);

        assertThat(balance.getId()).isEqualTo(7L);
        assertThat(balance.getMaterialId()).isEqualTo(100L);
        assertThat(balance.getOrgId()).isEqualTo(1L);
        assertThat(balance.getQuantityOnHand()).isEqualByComparingTo(new BigDecimal("10"));
        assertThat(balance.getUnit()).isEqualTo("PCS");
        assertThat(balance.getUnitCost()).isEqualByComparingTo(new BigDecimal("5.00"));
    }
}