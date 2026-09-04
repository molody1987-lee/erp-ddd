package com.company.erp.purchase.interfaces.rest.dto;

import lombok.Data;

/**
 * 关闭采购订单请求。orderId 以路径参数为准。
 */
@Data
public class ClosePurchaseOrderRequest {

    private Long orderId;
    private Long operatorId;
    private String reason;
}