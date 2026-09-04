package com.company.erp.purchase.domain.service;

import com.company.erp.purchase.domain.entity.PurchaseOrderItem;
import com.company.erp.purchase.domain.exception.DomainException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 采购订单领域服务：负责无法自然归属到聚合内的校验规则。
 * 物料有效性（是否存在）通过基础数据上下文的 OHS 在应用层校验，此处不涉及。
 */
public class PurchaseOrderValidator {

    public void validate(List<PurchaseOrderItem> items) {
        if (items == null || items.isEmpty()) {
            throw new DomainException("采购订单至少需要一个明细行");
        }
        Set<Long> materialIds = new HashSet<>();
        for (PurchaseOrderItem item : items) {
            if (!materialIds.add(item.getMaterialId().value())) {
                throw new DomainException("同一个物料不能重复出现: " + item.getMaterialId().value());
            }
        }
    }
}