package com.company.erp.purchase.interfaces.rest.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * 审核采购订单请求。
 */
public record ReviewPurchaseOrderRequest(
        @NotNull @Positive Long reviewerId) {
}