package com.company.erp.purchase.application.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 采购订单读模型。
 */
@Data
public class PurchaseOrderDTO {

    private Long id;
    private String orderCode;
    private Long supplierId;
    private Long orgId;
    private Integer status;
    private String statusText;
    private BigDecimal totalAmount;
    private String currency;
    private String remark;
    private LocalDateTime createTime;
    private List<PurchaseOrderItemDTO> items;
}