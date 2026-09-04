package com.company.erp.purchase.domain.valueobject;

/**
 * 入库数量值对象，用于接收入库命令。
 */
public record ReceivedQuantity(MaterialId materialId, Quantity quantity) {
}