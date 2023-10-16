package com.redis.core.command.impl;

import com.redis.core.command.HashCommands;
import com.redis.core.command.data.RedisDataDictFactory;
import com.redis.core.command.data.RedisHash;

public class HashCommandsImpl implements HashCommands {
    @Override
    public String hget(String k, String field) {
        final RedisHash redisHash = RedisDataDictFactory.get(k);
        if (null != redisHash) {
            return redisHash.getValue(field);
        }
        return "nil";
    }

    @Override
    public int hset(String k, String field, String v) {
        RedisHash redisHash = RedisDataDictFactory.get(k);
        int res = 0;
        if (null == redisHash) {
            res = 1;
            redisHash = new RedisHash();
            RedisDataDictFactory.put(k, redisHash);
        }
        redisHash.setValue(field, v);
        return res;
    }
}
