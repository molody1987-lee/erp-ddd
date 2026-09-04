package com.company.erp.inventory.domain.repository;

import com.company.erp.inventory.domain.aggregate.InventoryBalance;

import java.util.Optional;

/**
 * 库存结存仓储接口，定义在领域层。
 */
public interface InventoryBalanceRepository {

    Optional<InventoryBalance> findByMaterialAndOrg(Long materialId, Long orgId);

    /** 悲观行锁查询（SELECT ... FOR UPDATE），在事务内锁定现有结存行直至提交。 */
    Optional<InventoryBalance> findForUpdateByMaterialAndOrg(Long materialId, Long orgId);

    Long save(InventoryBalance balance);
}