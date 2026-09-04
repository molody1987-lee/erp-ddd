package com.company.erp.purchase.domain.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 采购入库领域事件（跨上下文通信契约）。
 */
public record PurchaseOrderReceivedEvent(
        String eventId,
        Long orgId,
        Long purchaseOrderId,
        List<Item> items,
        LocalDateTime occurredAt) {

    public record Item(Long materialId, BigDecimal quantity, String unit, BigDecimal unitPrice) {

        public Item {
            if (materialId == null || materialId <= 0) {
                throw new IllegalArgumentException("事件明细物料ID不能为空");
            }
            if (quantity == null || quantity.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("事件明细数量必须大于0");
            }
            if (unit == null || unit.isBlank()) {
                throw new IllegalArgumentException("事件明细单位不能为空");
            }
            if (unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("事件明细单价不能为负");
            }
        }
    }
}