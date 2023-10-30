package com.redis.handler;

import com.redis.resp.Resp;

import java.util.Arrays;

public class RedisReq {
    private final String commandName;
    private final Resp[] content;

    public RedisReq(String commandType, Resp[] content) {
        this.commandName = commandType;
        this.content = content;
    }

    public String getCommandName() {
        return commandName;
    }

    public Resp[] getContent() {
        return content;
    }

    @Override
    public String toString() {
        return "RedisData{" +
                "commandName='" + commandName + '\'' +
                ", content=" + Arrays.toString(content) +
                '}';
    }
}
