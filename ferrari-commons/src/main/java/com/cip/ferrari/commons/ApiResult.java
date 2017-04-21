package com.cip.ferrari.commons;

public class ApiResult<T> {

    public static final ApiResult<String> SUCCESS = new ApiResult<String>(null);
    public static final ApiResult<String> FAIL = new ApiResult<String>(500, null);

    private int code;
    private String msg;
    private T content;

    public ApiResult(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public ApiResult(T content) {
        this.code = 200;
        this.content = content;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getContent() {
        return content;
    }

    public void setContent(T content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "ReturnT [code=" + code + ", msg=" + msg + ", content=" + content + "]";
    }

}
