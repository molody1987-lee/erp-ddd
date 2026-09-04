package com.company.erp.purchase.interfaces.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

/**
 * 创建采购订单明细请求。
 */
public record CreatePurchaseOrderItemRequest(
        @NotNull Long materialId,
        @NotNull @Positive BigDecimal quantity,
        @NotBlank String unit,
        @NotNull @Positive BigDecimal unitPrice,
        @NotBlank String currency) {
}