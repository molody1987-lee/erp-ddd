package com.company.erp.inventory.application.query;

import com.company.erp.inventory.application.dto.InventoryBalanceDTO;

import java.util.Optional;

/**
 * 库存结存查询仓储端口（读模型）。
 */
public interface InventoryBalanceQueryRepository {

    Optional<InventoryBalanceDTO> findBalance(Long materialId, Long orgId);
}