package com.company.erp.purchase.interfaces.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 创建采购订单明细请求。
 */
@Data
public class CreatePurchaseOrderItemRequest {

    @NotNull(message = "物料ID不能为空")
    private Long materialId;

    @NotNull(message = "采购数量不能为空")
    private BigDecimal quantity;

    @NotBlank(message = "数量单位不能为空")
    private String unit;

    @NotNull(message = "单价不能为空")
    private BigDecimal unitPrice;
}