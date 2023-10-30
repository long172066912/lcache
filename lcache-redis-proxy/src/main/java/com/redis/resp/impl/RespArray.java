package com.redis.resp.impl;

import com.redis.resp.Resp;

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