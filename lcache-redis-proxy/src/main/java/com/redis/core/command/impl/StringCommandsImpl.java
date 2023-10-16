package com.redis.core.command.impl;

import com.redis.core.Resp;
import com.redis.core.command.StringCommands;
import com.redis.core.command.data.RedisDataDictFactory;
import com.redis.core.command.data.RedisString;
import com.redis.core.resp.SimpleString;

public class StringCommandsImpl implements StringCommands {
    @Override
    public Resp get(String k) {
        RedisString redisString = RedisDataDictFactory.get(k);
        if (null != redisString) {
            return new SimpleString(redisString.getV());
        }
        return new SimpleString("");
    }

    @Override
    public Resp set(String k, String v) {
        RedisString redisString = RedisDataDictFactory.get(k);
        if (null == redisString) {
            redisString = new RedisString(v);
            RedisDataDictFactory.put(k, redisString);
        } else {
            redisString.setV(v);
        }
        return SimpleString.OK;
    }
}
