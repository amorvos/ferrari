package com.cip.ferrari.commons;

import java.io.Serializable;

import com.google.common.base.Preconditions;

@SuppressWarnings("unchecked")
public class ApiResult<T> implements Serializable {

    private static final long serialVersionUID = 3801431673094687360L;

    private static final SuccApiResult SUCCESS = new SuccApiResult();
    private static final FailApiResult FAIL = new FailApiResult().msg("操作失败");

    public static final int DEFAULT_FAILED_STATUS = 500;
    public static final int DEFAULT_SUCCESS_STATUS = 200;

    private Integer code;

    private String msg;

    private T content;

    ApiResult() {
    }

    public ApiResult(int code, String msg) {
        this.code = code;
        this.msg = msg;
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

    public static <T> SuccApiResult<T> succ() {
        return SUCCESS;
    }

    public static <T> FailApiResult<T> fail() {
        return FAIL;
    }

    public static <T> ApiResult<T> succ(String msg, T content) {
        return ApiResult.<T> succ().msg(msg).content(content);
    }

    public static <T> ApiResult<T> fail(String errmsg) {
        return ApiResult.<T> fail().msg(errmsg);
    }

    public static <T> ApiResult<T> fail(int code, String errmsg) {
        return ApiResult.<T> fail().code(code).msg(errmsg);
    }

    public static <T> ApiResult<T> fail(T content) {
        return ApiResult.<T> fail().content(content);
    }

    public static <T> ApiResult<T> fail(int code, T content) {
        return ApiResult.<T> fail().code(code).content(content);
    }

    public static <T> ApiResult<T> fail(String msg, T content) {
        return ApiResult.<T> fail().msg(msg).content(content);
    }

    public static <T> ApiResult<T> fail(int code, String msg, T content) {
        return ApiResult.<T> fail().code(code).msg(msg).content(content);
    }

    /**
     * 成功的ApiResult
     */
    private static class SuccApiResult<T> extends ApiResult<T> {

        private static final long serialVersionUID = -5254346572757569280L;

        private SuccApiResult() {
            this.setCode(DEFAULT_SUCCESS_STATUS);
        }

        public SuccApiResult<T> msg(String msg) {
            this.setMsg(msg);
            return this;
        }

        public SuccApiResult<T> content(T content) {
            this.setContent(content);
            return this;
        }
    }

    /**
     * 失败的ApiResult
     */
    private static final class FailApiResult<T> extends ApiResult<T> {

        private static final long serialVersionUID = -5757851338890664660L;

        private FailApiResult() {
            super();
            this.setCode(DEFAULT_FAILED_STATUS);
        }

        public FailApiResult<T> code(int code) {
            Preconditions.checkArgument(code != DEFAULT_SUCCESS_STATUS, "code不能为200");
            this.setCode(code);
            return this;
        }

        public FailApiResult<T> msg(String msg) {
            this.setMsg(msg);
            return this;
        }

        public FailApiResult<T> content(T content) {
            this.setContent(content);
            return this;
        }
    }
}
