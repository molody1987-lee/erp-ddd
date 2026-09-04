package com.company.erp.purchase.interfaces.rest.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

/**
 * 修改采购订单请求。
 */
public record ModifyPurchaseOrderRequest(
        @NotBlank String orderCode,
        @NotNull @Positive Long supplierId,
        @NotEmpty List<@Valid CreatePurchaseOrderItemRequest> items) {
}