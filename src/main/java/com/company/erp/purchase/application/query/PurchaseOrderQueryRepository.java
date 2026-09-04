package com.company.erp.purchase.application.query;

import com.company.erp.purchase.application.dto.PurchaseOrderDTO;
import com.company.erp.purchase.domain.valueobject.PurchaseOrderId;

import java.util.List;
import java.util.Optional;

/**
 * 采购订单查询仓储端口（读模型）。
 */
public interface PurchaseOrderQueryRepository {

    Optional<PurchaseOrderDTO> findDetail(PurchaseOrderId id);

    List<PurchaseOrderDTO> findByOrgId(Long orgId);
}