package com.company.erp.purchase.interfaces.rest;

import lombok.Data;

/**
 * 统一 API 响应包装。
 */
@Data
public class CommonResponse<T> {

    private int code;
    private String message;
    private T data;

    public static <T> CommonResponse<T> ok(T data) {
        CommonResponse<T> r = new CommonResponse<>();
        r.code = 0;
        r.message = "success";
        r.data = data;
        return r;
    }

    public static <T> CommonResponse<T> error(int code, String message) {
        CommonResponse<T> r = new CommonResponse<>();
        r.code = code;
        r.message = message;
        return r;
    }
}