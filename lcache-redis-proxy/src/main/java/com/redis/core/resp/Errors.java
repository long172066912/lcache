package com.redis.core.resp;

import com.redis.core.Resp;

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