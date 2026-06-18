package com.example.demo.entity;

import lombok.Data;

@Data
public class Result<T> {
    private int code;
    private String msg;
    private T data;

    private Result(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(200, "success", data);
    }

    public static <T> Result<T> error(String msg) {
        return new Result<>(500, msg, null);
    }

    /**
     * 异常友好型 error：message 为 null 时降级为异常类名，防止前端收到空消息。
     * 调用方需已在 Controller 层自行打印 log.error(prefix, e) 记录完整堆栈。
     */
    public static <T> Result<T> error(String prefix, Throwable e) {
        String msg = prefix + ": " + (e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage());
        return new Result<>(500, msg, null);
    }
}