package com.redis.core.command.impl;

import com.redis.core.command.StringCommands;
import com.redis.core.command.data.RedisDataDictFactory;
import com.redis.core.command.data.RedisString;

public class StringCommandsImpl implements StringCommands {
    @Override
    public String get(String k) {
        RedisString redisString = RedisDataDictFactory.get(k);
        if (null != redisString) {
            return redisString.getV();
        }
        return null;
    }

    @Override
    public String set(String k, String v) {
        RedisString redisString = RedisDataDictFactory.get(k);
        if (null == redisString) {
            redisString = new RedisString(v);
            RedisDataDictFactory.put(k, redisString);
        } else {
            redisString.setV(v);
        }
        return "OK";
    }
}
