package com.company.erp.purchase.application.command;

import java.math.BigDecimal;

/**
 * 采购入库明细行命令。
 */
public record ReceiveItemCommand(Long materialId, BigDecimal quantity, String unit) {
}