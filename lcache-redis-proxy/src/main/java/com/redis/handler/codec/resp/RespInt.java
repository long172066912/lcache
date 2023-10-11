package com.redis.handler.codec.resp;

import com.redis.handler.codec.Resp;

public class RespInt implements Resp {
    int value;

    public RespInt(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}