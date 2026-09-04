package com.company.erp.purchase.interfaces.rest.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * 创建采购订单请求。
 */
@Data
public class CreatePurchaseOrderRequest {

    @NotNull(message = "供应商ID不能为空")
    private Long supplierId;

    @NotNull(message = "组织ID不能为空")
    private Long orgId;

    @NotBlank(message = "币种不能为空")
    private String currency = "CNY";

    @Size(max = 500, message = "备注长度不能超过500")
    private String remark;

    private Long createdBy;

    @Valid
    @NotEmpty(message = "采购明细不能为空")
    private List<CreatePurchaseOrderItemRequest> items;
}