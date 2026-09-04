package com.company.erp.purchase.interfaces.rest.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.List;

/**
 * 采购入库请求。
 */
public record ReceivePurchaseOrderRequest(
        @NotEmpty List<@Valid ReceiveItemRequest> items) {

    public record ReceiveItemRequest(
            @NotNull @Positive Long materialId,
            @NotNull @Positive BigDecimal quantity,
            @NotNull String unit) {
    }
}