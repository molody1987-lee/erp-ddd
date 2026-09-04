package com.company.erp.purchase.application.query;

/**
 * 采购订单分页查询条件。
 */
public record PurchaseOrderPageQuery(long pageNum, long pageSize, Integer status, Long supplierId) {

    public PurchaseOrderPageQuery {
        if (pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize < 1) {
            pageSize = 20;
        }
        if (pageSize > 200) {
            pageSize = 200;
        }
    }
}