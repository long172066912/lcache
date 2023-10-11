package com.redis.core;

import com.redis.core.resp.RespArray;
import com.redis.core.resp.RespInt;
import com.redis.core.resp.SimpleString;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum CommandType {
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

    private static Map<String, CommandType> map = Arrays.stream(CommandType.values()).collect(Collectors.toMap(CommandType::getCommand, v -> v));

    public String getCommand() {
        return command;
    }

    public RespArray getDesc() {
        return desc;
    }

    public static CommandType nameOf(String commandName) {
        return map.get(commandName);
    }
}
