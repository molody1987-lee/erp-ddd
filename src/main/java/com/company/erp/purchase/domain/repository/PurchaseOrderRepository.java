package com.company.erp.purchase.domain.repository;

import com.company.erp.purchase.domain.aggregate.PurchaseOrder;
import com.company.erp.purchase.domain.valueobject.PurchaseOrderId;

import java.util.Optional;

/**
 * 采购订单仓储接口，定义在领域层，面向聚合根。
 */
public interface PurchaseOrderRepository {

    Optional<PurchaseOrder> findById(PurchaseOrderId id);

    /** 保存聚合，返回持久化后的订单ID（新建时回填雪花ID）。 */
    PurchaseOrderId save(PurchaseOrder order);
}