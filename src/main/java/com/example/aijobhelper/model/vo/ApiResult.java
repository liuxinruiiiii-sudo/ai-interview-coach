package com.example.aijobhelper.model.vo;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 统一返回结果
 * <p>
 * 使用方式：
 * <pre>{@code
 *   return ApiResult.ok(data);
 *   return ApiResult.fail("参数错误");
 * }</pre>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResult<T> {

    private int code;
    private String message;
    private T data;

    private ApiResult() {
    }

    private ApiResult(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    // ──────────── 成功 ────────────

    public static <T> ApiResult<T> ok() {
        return new ApiResult<>(200, "success", null);
    }

    public static <T> ApiResult<T> ok(T data) {
        return new ApiResult<>(200, "success", data);
    }

    public static <T> ApiResult<T> ok(String message, T data) {
        return new ApiResult<>(200, message, data);
    }

    // ──────────── 失败 ────────────

    public static <T> ApiResult<T> fail() {
        return new ApiResult<>(500, "服务器内部错误", null);
    }

    public static <T> ApiResult<T> fail(String message) {
        return new ApiResult<>(500, message, null);
    }

    public static <T> ApiResult<T> fail(int code, String message) {
        return new ApiResult<>(code, message, null);
    }

    // ──────────── getter / setter ────────────

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
