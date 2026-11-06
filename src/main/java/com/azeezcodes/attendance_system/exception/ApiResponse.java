package com.azeezcodes.attendance_system.exception;

import lombok.Data;
import lombok.Getter;

@Data
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private int statusCode;
    private T data;

    public static <T> ApiResponse<T> success(String message, int statusCode, T data){
        ApiResponse<T> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setMessage(message);
        response.setStatusCode(statusCode);
        response.setData(data);
        return response;
    }

    public static <T> ApiResponse<T> error(String message, int statusCode){
        ApiResponse<T> response = new ApiResponse<>();
        response.setSuccess(false);
        response.setMessage(message);
        response.setStatusCode(statusCode);
        return response;
    }
}


