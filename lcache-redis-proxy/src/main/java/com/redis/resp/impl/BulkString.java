package com.redis.resp.impl;

import com.redis.resp.Resp;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * 多行字符串
 */
public class BulkString implements Resp {
    public static final BulkString NullBulkString = new BulkString(null);
    static final Charset CHARSET = StandardCharsets.UTF_8;
    BytesWrapper content;

    public BulkString(BytesWrapper content) {
        this.content = content;
    }

    public BytesWrapper getContent() {
        return content;
    }
}