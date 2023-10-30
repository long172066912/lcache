package com.redis.core.command.impl;

import com.redis.resp.Resp;
import com.redis.core.command.CommonRedisCommands;
import com.redis.core.command.RedisDataDict;
import com.redis.core.command.data.RedisDataDictFactory;
import com.redis.resp.impl.RespArray;
import com.redis.resp.impl.RespInt;
import com.redis.resp.impl.SimpleString;

import java.util.*;
import java.util.stream.Collectors;

public class CommonRedisCommandsImpl implements CommonRedisCommands {

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
    public Resp command() {
        final List<RespArray> collect = Arrays.stream(CommandType.values()).filter(e -> null != e.getDesc()).map(CommandType::getDesc).collect(Collectors.toList());
        return new RespArray(collect.toArray(new RespArray[]{}));
    }

    @Override
    public Resp hello() {
        final Resp[] resps = new Resp[map.size()];
        int i = 0;
        for (Map.Entry<Resp, Resp> respRespEntry : map.entrySet()) {
            resps[i] = new RespArray(new Resp[]{respRespEntry.getKey(), respRespEntry.getValue()});
            i++;
        }
        return new RespArray(resps);
    }

    @Override
    public Resp expire(String k, Integer expireTime) {
        Optional.ofNullable(RedisDataDictFactory.get(k)).ifPresent(data -> ((RedisDataDict) data).setExpireTime(expireTime));
        return SimpleString.OK;
    }

    private enum CommandType {
        /**
         * STRING
         */
        HELLO("hello", null),
        COMMAND("command", null),
        GET("get", new RespArray(new Resp[]{
                new RespInt(2), new RespArray(new Resp[]{new SimpleString("readonly"), new SimpleString("fast")}), new RespInt(1), new RespInt(1), new RespInt(1)
        })),

        SET("set", new RespArray(new Resp[]{
                new RespInt(-3), new RespArray(new Resp[]{new SimpleString("write"), new SimpleString("denyoom")}), new RespInt(1), new RespInt(1), new RespInt(1)
        })),

        /**
         * HASH
         */
        HGET("hget", new RespArray(new Resp[]{
                new RespInt(2), new RespArray(new Resp[]{new SimpleString("readonly"), new SimpleString("fast")}), new RespInt(1), new RespInt(1), new RespInt(1)
        })),

        HSET("hset", new RespArray(new Resp[]{
                new RespInt(4), new RespArray(new Resp[]{new SimpleString("readonly"), new SimpleString("denyoom"), new SimpleString("fast")}), new RespInt(1), new RespInt(1), new RespInt(1)
        })),
        ;

        private String command;
        private RespArray desc;

        CommandType(String command, RespArray desc) {
            this.command = command;
            this.desc = desc;
        }

        public String getCommand() {
            return command;
        }

        public RespArray getDesc() {
            return desc;
        }
    }

}
