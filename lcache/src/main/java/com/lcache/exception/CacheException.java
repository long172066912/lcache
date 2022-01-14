package com.lcache.exception;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: CacheException
 * @Description: 异常封装对象
 * @date 2021/2/23 3:41 PM
 */
public class CacheException extends RuntimeException {

    private int code;

    private String msg;

    private Object data;

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

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public CacheException(int code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    @Override
    public Throwable fillInStackTrace() {
        return this;
    }
}
