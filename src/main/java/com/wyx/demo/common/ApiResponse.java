package com.wyx.demo.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private int code;
    private T data;
    private Object error;
    private String message;

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(0, data, null, "ok");
    }

    public static <T> ApiResponse<T> error(int code, String message) {
        return new ApiResponse<>(code, null, message, message);
    }
}
