package com.redis.handler.codec.resp;

import com.redis.handler.codec.Resp;

/**
 * 错误信息返回
 */
public class Errors implements Resp {
    String content;

    public Errors(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }
}