package com.company.erp.purchase.application.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 采购订单读模型。
 */
public record PurchaseOrderDTO(
        Long id,
        String orderCode,
        Long supplierId,
        Long orgId,
        String status,
        List<PurchaseOrderItemDTO> items,
        LocalDateTime createdAt) {
}