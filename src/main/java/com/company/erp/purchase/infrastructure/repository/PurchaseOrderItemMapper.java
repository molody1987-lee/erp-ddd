package com.company.erp.purchase.infrastructure.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.company.erp.purchase.infrastructure.persistence.PurchaseOrderItemPO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 采购订单明细 Mapper。
 */
@Mapper
public interface PurchaseOrderItemMapper extends BaseMapper<PurchaseOrderItemPO> {
}