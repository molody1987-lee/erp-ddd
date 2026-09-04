package com.company.erp.purchase.infrastructure.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.company.erp.purchase.infrastructure.persistence.PurchaseOrderPO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 采购订单 Mapper。
 */
@Mapper
public interface PurchaseOrderMapper extends BaseMapper<PurchaseOrderPO> {
}