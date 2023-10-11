package com.redis.core.impl;

import com.redis.handler.codec.Resp;
import com.redis.core.command.RedisData;
import com.redis.handler.codec.resp.RespArray;
import com.redis.handler.codec.resp.RespInt;
import com.redis.handler.codec.resp.SimpleString;
import com.redis.handler.CommandHandler;

import java.util.HashMap;
import java.util.Map;

public class HelloHandlerImpl implements CommandHandler {
    private static final Map<Resp, Resp> map = new HashMap<>();

    static {
        map.put(new SimpleString("server"), new SimpleString("redis"));
        map.put(new SimpleString("version"), new SimpleString("6.0.6"));
        map.put(new SimpleString("proto"), new RespInt(3));
        map.put(new SimpleString("id"), new RespInt(4));
        map.put(new SimpleString("mode"), new SimpleString("standalone"));
        map.put(new SimpleString("role"), new SimpleString("master"));
    }

    @Override
    public Resp handle(RedisData redisData) {
        System.out.println("执行Hello命令 : " + redisData);
        final Resp[] resps = new Resp[map.size()];
        int i = 0;
        for (Map.Entry<Resp, Resp> respRespEntry : map.entrySet()) {
            resps[i] = new RespArray(new Resp[]{respRespEntry.getKey(), respRespEntry.getValue()});
            i++;
        }
        return new RespArray(resps);
    }
}
