package com.redis.handler.codec.resp;

import com.redis.handler.codec.Resp;

/**
 * 数组
 */
public class RespArray implements Resp {

    Resp[] array;

    public RespArray(Resp[] array) {
        this.array = array;
    }

    public Resp[] getArray() {
        return array;
    }
}