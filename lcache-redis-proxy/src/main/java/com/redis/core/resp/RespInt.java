package com.redis.core.resp;

import com.redis.core.Resp;

public class RespInt implements Resp {
    int value;

    public RespInt(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}