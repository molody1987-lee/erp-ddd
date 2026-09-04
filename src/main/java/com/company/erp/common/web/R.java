package com.company.erp.common.web;

/**
 * 统一响应包装。
 */
public record R<T>(int code, String message, T data) {

    public static <T> R<T> ok(T data) {
        return new R<>(0, "success", data);
    }

    public static R<Void> ok() {
        return new R<>(0, "success", null);
    }

    public static <T> R<T> fail(int code, String message) {
        return new R<>(code, message, null);
    }
}