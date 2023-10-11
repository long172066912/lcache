package com.redis.core.impl;

import com.redis.core.CommandType;
import com.redis.handler.codec.Resp;
import com.redis.core.command.RedisData;
import com.redis.core.data.RedisHash;
import com.redis.handler.codec.resp.RespInt;
import com.redis.handler.codec.resp.SimpleString;
import com.redis.handler.CommandHandler;
import com.redis.utils.CommonUtil;
import com.redis.utils.RespUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HashHandlerImpl implements CommandHandler {

    private static Map<String, RedisHash> data = new ConcurrentHashMap<>();

    @Override
    public Resp handle(RedisData redisData) {
        System.out.println("执行Hash命令 : " + redisData);
        final String k = RespUtil.toString(redisData.getContent()[1]);
        final String filed = redisData.getContent().length > 2 ? RespUtil.toString(redisData.getContent()[2]) : null;
        switch (CommandType.nameOf(redisData.getCommandName())) {
            case HGET:
                final String value = CommonUtil.getValue(data, k, filed);
                if (CommonUtil.isBlank(value)) {
                    return SimpleString.NIL;
                }
                return new SimpleString(value);
            case HSET:
                final String v = RespUtil.toString(redisData.getContent()[3]);
                //判断是否是创建
                int res = CommonUtil.isNotBlank(CommonUtil.getValue(data, k, filed)) ? 0 : 1;
                data.computeIfAbsent(k, e -> new RedisHash()).setValue(filed, v);
                return new RespInt(res);
            default:
                break;
        }
        return null;
    }
}
