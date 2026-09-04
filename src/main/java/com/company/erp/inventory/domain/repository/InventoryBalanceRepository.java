package com.company.erp.inventory.domain.repository;

import com.company.erp.inventory.domain.aggregate.InventoryBalance;

import java.util.Optional;

/**
 * 库存结存仓储接口（面向聚合根）。
 */
public interface InventoryBalanceRepository {

    Optional<InventoryBalance> findByMaterialAndOrg(Long materialId, Long orgId);

    Optional<InventoryBalance> findForUpdateByMaterialAndOrg(Long materialId, Long orgId);

    Long save(InventoryBalance balance);
}