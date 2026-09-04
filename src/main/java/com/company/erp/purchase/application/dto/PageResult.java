package com.company.erp.purchase.application.dto;

import java.util.List;

/**
 * 分页结果。
 */
public record PageResult<T>(List<T> records, long total, long pageNum, long pageSize) {
}