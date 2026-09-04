package com.company.erp.inventory.infrastructure.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.company.erp.inventory.infrastructure.persistence.InventoryBalancePO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 库存结存 Mapper。
 */
@Mapper
public interface InventoryBalanceMapper extends BaseMapper<InventoryBalancePO> {
}