package com.redis.core.impl;

import com.redis.core.CommandType;
import com.redis.core.Resp;
import com.redis.core.command.RedisData;
import com.redis.core.resp.RespArray;
import com.redis.handler.CommandHandler;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CommandHandlerImpl implements CommandHandler {

    @Override
    public Resp handle(RedisData redisData) {
        final List<RespArray> collect = Arrays.stream(CommandType.values()).filter(e -> null != e.getDesc()).map(CommandType::getDesc).collect(Collectors.toList());
        System.out.println("执行Command命令 : " + redisData + ", list : " + collect);
        return new RespArray(collect.toArray(new RespArray[]{}));
    }
}
