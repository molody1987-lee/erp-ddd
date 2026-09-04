package com.company.erp.purchase.application.query;

import com.company.erp.purchase.application.dto.PageResult;
import com.company.erp.purchase.application.dto.PurchaseOrderDTO;

import java.util.Optional;

/**
 * 采购订单查询仓储端口（读模型），由基础设施层实现。
 */
public interface PurchaseOrderQueryRepository {

    Optional<PurchaseOrderDTO> findById(Long id);

    PageResult<PurchaseOrderDTO> page(PurchaseOrderPageQuery query);
}