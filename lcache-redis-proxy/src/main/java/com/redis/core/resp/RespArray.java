package com.redis.core.resp;

import com.redis.core.Resp;

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