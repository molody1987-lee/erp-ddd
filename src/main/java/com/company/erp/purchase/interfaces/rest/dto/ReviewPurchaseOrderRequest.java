package com.company.erp.purchase.interfaces.rest.dto;

import lombok.Data;

/**
 * 审核采购订单请求。orderId 以路径参数为准。
 */
@Data
public class ReviewPurchaseOrderRequest {

    private Long orderId;
    private Long operatorId;
}