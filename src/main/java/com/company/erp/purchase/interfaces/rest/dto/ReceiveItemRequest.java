package com.company.erp.purchase.interfaces.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 采购入库明细请求。
 */
@Data
public class ReceiveItemRequest {

    @NotNull(message = "物料ID不能为空")
    private Long materialId;

    @NotNull(message = "入库数量不能为空")
    private BigDecimal quantity;

    @NotBlank(message = "数量单位不能为空")
    private String unit;
}