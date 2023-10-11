package com.redis.core.impl;

import com.redis.core.CommandType;
import com.redis.core.Resp;
import com.redis.core.command.RedisData;
import com.redis.core.data.RedisString;
import com.redis.core.resp.SimpleString;
import com.redis.handler.CommandHandler;
import com.redis.utils.RespUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class StringHandlerImpl implements CommandHandler {

    private static Map<String, RedisString> kvs = new ConcurrentHashMap<>();

    @Override
    public Resp handle(RedisData redisData) {
        System.out.println("执行String命令 : " + redisData);
        final String k = RespUtil.toString(redisData.getContent()[1]);
        final String v = redisData.getContent().length > 2 ? RespUtil.toString(redisData.getContent()[2]) : null;
        switch (CommandType.nameOf(redisData.getCommandName())) {
            case GET:
                final RedisString redisString = kvs.get(k);
                if (null != redisString) {
                    if (redisString.isExpire()) {
                        kvs.remove(k);
                        return new SimpleString("");
                    }
                    return new SimpleString(redisString.getV());
                } else {
                    return new SimpleString("");
                }
            case SET:
                final RedisString data = new RedisString(v);
                if (redisData.getContent().length > 3) {
                    data.setExpireTime(RespUtil.toInt(redisData.getContent()[3]));
                } else {
                    data.setExpireTime(Integer.MAX_VALUE);
                }
                kvs.put(k, data);
            default:
                break;
        }
        return SimpleString.OK;
    }
}
