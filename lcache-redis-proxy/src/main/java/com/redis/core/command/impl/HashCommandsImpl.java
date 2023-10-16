package com.redis.core.command.impl;

import com.redis.core.Resp;
import com.redis.core.command.HashCommands;
import com.redis.core.command.data.RedisDataDictFactory;
import com.redis.core.command.data.RedisHash;
import com.redis.core.resp.RespInt;
import com.redis.core.resp.SimpleString;

public class HashCommandsImpl implements HashCommands {
    @Override
    public Resp hget(String k, String field) {
        final RedisHash redisHash = RedisDataDictFactory.get(k);
        if (null != redisHash) {
            return new SimpleString(redisHash.getValue(field));
        }
        return SimpleString.NIL;
    }

    @Override
    public Resp hset(String k, String field, String v) {
        RedisHash redisHash = RedisDataDictFactory.get(k);
        int res = 0;
        if (null == redisHash) {
            res = 1;
            redisHash = new RedisHash();
            RedisDataDictFactory.put(k, redisHash);
        }
        redisHash.setValue(field, v);
        return new RespInt(res);
    }
}
