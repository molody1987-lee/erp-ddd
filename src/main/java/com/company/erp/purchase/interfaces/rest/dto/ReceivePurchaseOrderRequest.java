package com.company.erp.purchase.interfaces.rest.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * 采购入库请求。orderId 以路径参数为准。
 */
@Data
public class ReceivePurchaseOrderRequest {

    private Long orderId;

    @Valid
    @NotEmpty(message = "入库明细不能为空")
    private List<ReceiveItemRequest> items;
}