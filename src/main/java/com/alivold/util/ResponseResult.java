package com.alivold.util;

public class ResponseResult {
    private int code;
    private String message;
    private Object data; // 使用Object作为数据类型

    public ResponseResult(int code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

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

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    // 静态方法，用于构建成功响应的ResponseResult对象
    public static ResponseResult success(Object data) {
        return new ResponseResult(200, "成功", data);
    }

    public static ResponseResult success(){
        return new ResponseResult(200, "成功", null);
    }

    // 静态方法，用于构建失败响应的ResponseResult对象
    public static ResponseResult fail() {
        return new ResponseResult(500, "失败", null);
    }

    public static ResponseResult fail(String msg) {
        return new ResponseResult(500, msg, null);
    }

    public static ResponseResult fail(int code, String msg) {
        return new ResponseResult(code, msg, null);
    }
}
