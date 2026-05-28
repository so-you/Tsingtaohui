package com.tsingtaohui.common.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private String code;
    private String message;
    private T data;
    private String requestId;

    private ApiResponse(String code, String message, T data, String requestId) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.requestId = requestId;
    }

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>("0", "OK", data, null);
    }

    public static <T> ApiResponse<T> ok(T data, String requestId) {
        return new ApiResponse<>("0", "OK", data, requestId);
    }

    public static <T> ApiResponse<T> error(String code, String message) {
        return new ApiResponse<>(code, message, null, null);
    }

    public static <T> ApiResponse<T> error(String code, String message, String requestId) {
        return new ApiResponse<>(code, message, null, requestId);
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

    public String getRequestId() {
        return requestId;
    }
}
