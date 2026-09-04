package com.company.erp.inventory.infrastructure.repository;

import com.company.erp.inventory.domain.aggregate.InventoryBalance;
import com.company.erp.inventory.domain.valueobject.InventoryQuantity;
import com.company.erp.inventory.infrastructure.persistence.InventoryBalancePO;
import org.springframework.stereotype.Component;

/**
 * 库存结存领域模型与持久化对象之间的转换器。
 */
@Component
public class InventoryBalanceConverter {

    public InventoryBalancePO toPO(InventoryBalance balance) {
        InventoryBalancePO po = new InventoryBalancePO();
        if (balance.getId() != null) {
            po.setId(balance.getId());
        }
        po.setMaterialId(balance.getMaterialId());
        po.setOrgId(balance.getOrgId());
        po.setQuantityOnHand(balance.getQuantityOnHand());
        po.setUnit(balance.getUnit());
        po.setUnitCost(balance.getUnitCost());
        po.setCreateTime(balance.getCreatedAt());
        po.setUpdateTime(balance.getUpdatedAt());
        return po;
    }

    public InventoryBalance toDomain(InventoryBalancePO po) {
        return InventoryBalance.reconstitute(
                po.getId(),
                po.getMaterialId(),
                po.getOrgId(),
                InventoryQuantity.of(po.getQuantityOnHand(), po.getUnit()),
                po.getUnitCost(),
                po.getCreateTime(),
                po.getUpdateTime());
    }
}