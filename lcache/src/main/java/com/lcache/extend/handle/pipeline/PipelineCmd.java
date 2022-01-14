package com.lcache.extend.handle.pipeline;

public abstract class PipelineCmd {

    protected CMD cmd;

    public enum CMD {
        GET,
        GETSET,
        SET,
        DEL,
        EXPIRE,
        SADD,
        SCARD,
        SREM,
        SPOP,
        HINCRBY,
        INCR,
        DECR,
        HSET,
        HGET,
        HGETALL,
        HDEL,
        SISMEMBER,
        HMSET,
        ZADD,
        ZADDBATCH,
        ZINCRBY,
        ZINCRBYDOUBLE,
        ZREM,
        ZSCORE,
        PTTL,
        LPUSH,
        LSET,
        RPUSH,
        HEXISTS,
        HMGET,
        ZRANGE_WITH_SCORES,
        ZCARD,
        EXPIREAT,
        ZREM_RANGE_BY_RANK,
        ZREM_RANGE_BY_SCORE,
        ZREVRANGE,
    }

    public abstract CMD getCmd();
}
