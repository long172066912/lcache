package com.redis.handler.codec.resp;

import com.redis.handler.codec.Resp;

/**
 * 简单字符串
 */
public class SimpleString implements Resp {
    public static final SimpleString OK = new SimpleString("OK");
    public static final SimpleString NIL = new SimpleString("nil");
    private final String content;

    public SimpleString(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }
}