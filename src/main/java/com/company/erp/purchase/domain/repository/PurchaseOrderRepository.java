package com.company.erp.purchase.domain.repository;

import com.company.erp.purchase.domain.aggregate.PurchaseOrder;
import com.company.erp.purchase.domain.valueobject.PurchaseOrderId;

import java.util.Optional;

/**
 * 采购订单仓储接口（面向聚合根）。
 */
public interface PurchaseOrderRepository {

    PurchaseOrderId save(PurchaseOrder order);

    Optional<PurchaseOrder> findById(PurchaseOrderId id);

    void deleteItems(PurchaseOrderId orderId);
}