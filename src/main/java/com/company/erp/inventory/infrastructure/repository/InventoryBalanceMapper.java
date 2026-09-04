package com.company.erp.inventory.infrastructure.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.company.erp.inventory.infrastructure.persistence.InventoryBalancePO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 库存结存 Mapper。
 */
@Mapper
public interface InventoryBalanceMapper extends BaseMapper<InventoryBalancePO> {

    @Select("SELECT * FROM inventory_balance WHERE material_id = #{materialId} AND org_id = #{orgId} FOR UPDATE")
    InventoryBalancePO selectForUpdate(@Param("materialId") Long materialId, @Param("orgId") Long orgId);
}