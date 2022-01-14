package com.lcache.extend.handle.redis.lettuce.pipeline;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.lcache.exception.CacheExceptionFactory;
import com.lcache.extend.handle.pipeline.*;
import io.lettuce.core.RedisFuture;
import io.lettuce.core.cluster.api.async.RedisClusterAsyncCommands;

import java.util.List;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: LettucePipelineExecutor
 * @Description: Lettuce管道实现
 * @date 2021/2/3 11:43 AM
 */
public class LettucePipelineExecutor {

    private RedisClusterAsyncCommands pipeline;

    public LettucePipelineExecutor(RedisClusterAsyncCommands redisClusterCommands) {
        this.pipeline = redisClusterCommands;
    }

    public List<RedisFuture<?>> pSync(List<PipelineCmd> commands) {
        List<RedisFuture<?>> futures = Lists.newArrayList();
        try {
            pipeline.setAutoFlushCommands(false);
            for (PipelineCmd pipelineCmd : commands) {
                if (pipelineCmd == null || pipelineCmd.getCmd() == null) {
                    continue;
                }
                switch (pipelineCmd.getCmd()) {
                    case SET:
                        if (((PipelineSet) pipelineCmd).getTime() < 0) {
                            futures.add(pipeline.set(((PipelineSet) pipelineCmd).getKey(), ((PipelineSet) pipelineCmd).getValue()));
                        } else {
                            futures.add(pipeline.setex(((PipelineSet) pipelineCmd).getKey(), ((PipelineSet) pipelineCmd).getTime(), ((PipelineSet) pipelineCmd).getValue()));
                        }
                        break;
                    case GET:
                        futures.add(pipeline.get(((PipelineGet) pipelineCmd).getKey()));
                        break;
                    case DEL:
                        String[] keys = ((PipelineDel) pipelineCmd).getKeys();
                        futures.add(pipeline.del(keys));
                        break;
                    case EXPIRE:
                        futures.add(pipeline.expire(((PipelineExpire) pipelineCmd).getKey(), ((PipelineExpire) pipelineCmd).getTime()));
                        break;
                    case SADD:
                        futures.add(pipeline.sadd(((PipelineSAdd) pipelineCmd).getKey(), ((PipelineSAdd) pipelineCmd).getMembers()));
                        break;
                    case SREM:
                        futures.add(pipeline.srem(((PipelineSRem) pipelineCmd).getKey(), ((PipelineSRem) pipelineCmd).getMembers()));
                        break;
                    case SISMEMBER:
                        futures.add(pipeline.sismember(((PipelineSIsmember) pipelineCmd).getKey(), ((PipelineSIsmember) pipelineCmd).getMember()));
                        break;
                    case SCARD:
                        futures.add(pipeline.scard(((PipelineScard) pipelineCmd).getKey()));
                        break;
                    case HSET:
                        futures.add(pipeline.hset(((PipelineHSet) pipelineCmd).getKey(), ((PipelineHSet) pipelineCmd).getField(), ((PipelineHSet) pipelineCmd).getValue()));
                        break;
                    case HDEL:
                        futures.add(pipeline.hdel(((PipelineHDel) pipelineCmd).getKey(), ((PipelineHDel) pipelineCmd).getField()));
                        break;
                    case HMSET:
                        futures.add(pipeline.hmset(((PipelineHMSet) pipelineCmd).getKey(), ((PipelineHMSet) pipelineCmd).getValues()));
                        break;
                    case HGET:
                        futures.add(pipeline.hget(((PipelineHGet) pipelineCmd).getKey(), ((PipelineHGet) pipelineCmd).getField()));
                        break;
                    case HGETALL:
                        futures.add(pipeline.hgetall(((PipelineHGetAll) pipelineCmd).getKey()));
                        break;
                    case HINCRBY:
                        futures.add(pipeline.hincrby(((PipelineHincrBy) pipelineCmd).getKey(), ((PipelineHincrBy) pipelineCmd).getField(), ((PipelineHincrBy) pipelineCmd).getValue()));
                        break;
                    case ZADD:
                        futures.add(pipeline.zadd(((PipelineZadd) pipelineCmd).getKey(), ((PipelineZadd) pipelineCmd).getScore(), ((PipelineZadd) pipelineCmd).getMember()));
                        break;
                    case ZADDBATCH:
                        futures.add(pipeline.zadd(((PipelineZaddBatch) pipelineCmd).getKey(), ((PipelineZaddBatch) pipelineCmd).getScoreMembers()));
                        break;
                    case ZINCRBY:
                        futures.add(pipeline.zincrby(((PipelineZincrby) pipelineCmd).getKey(), ((PipelineZincrby) pipelineCmd).getCount(), ((PipelineZincrby) pipelineCmd).getMember()));
                        break;
                    case ZINCRBYDOUBLE:
                        futures.add(pipeline.zincrby(((PipelineZincrbyDouble) pipelineCmd).getKey(), ((PipelineZincrbyDouble) pipelineCmd).getCount(), ((PipelineZincrbyDouble) pipelineCmd).getMember()));
                        break;
                    case ZREM:
                        futures.add(pipeline.zrem(((PipelineZREM) pipelineCmd).getKey(), ((PipelineZREM) pipelineCmd).getMember()));
                        break;
                    case ZSCORE:
                        futures.add(pipeline.zscore(((PipelineZScore) pipelineCmd).getKey(), ((PipelineZScore) pipelineCmd).getMember()));
                        break;
                    case PTTL:
                        futures.add(pipeline.pttl(((PipelinePTtl) pipelineCmd).getKey()));
                        break;
                    case LPUSH:
                        futures.add(pipeline.lpush(((PipelineLPush) pipelineCmd).getKey(), ((PipelineLPush) pipelineCmd).getFields()));
                        break;
                    case RPUSH:
                        futures.add(pipeline.rpush(((PipelineRPush) pipelineCmd).getKey(), ((PipelineRPush) pipelineCmd).getFields()));
                        break;
                    case LSET:
                        futures.add(pipeline.lset(((PipelineLSet) pipelineCmd).getKey(), ((PipelineLSet) pipelineCmd).getIndex(), ((PipelineLSet) pipelineCmd).getValue()));
                        break;
                    case INCR:
                        futures.add(pipeline.incrby(((PipelineIncr) pipelineCmd).getKey(), ((PipelineIncr) pipelineCmd).getIncrCount()));
                        break;
                    case HEXISTS:
                        futures.add(pipeline.hexists(((PipelineHExists) pipelineCmd).getKey(), ((PipelineHExists) pipelineCmd).getField()));
                        break;
                    case HMGET:
                        futures.add(pipeline.hmget(((PipelineHMGet) pipelineCmd).getKey(), ((PipelineHMGet) pipelineCmd).getFields()));
                        break;
                    case ZRANGE_WITH_SCORES:
                        futures.add(pipeline.zrangeWithScores(((PipelineZrangeWithScores) pipelineCmd).getKey(), ((PipelineZrangeWithScores) pipelineCmd).getStart(), ((PipelineZrangeWithScores) pipelineCmd).getEnd()));
                        break;
                    case ZCARD:
                        futures.add(pipeline.zcard(((PipelineZcard) pipelineCmd).getKey()));
                        break;
                    case EXPIREAT:
                        futures.add(pipeline.expireat(((PipelineExpireAt) pipelineCmd).getKey(), ((PipelineExpireAt) pipelineCmd).getUnixTime()));
                        break;
                    case ZREM_RANGE_BY_RANK:
                        futures.add(pipeline.zremrangebyrank(((PipelineZremRangeByRank) pipelineCmd).getKey(), ((PipelineZremRangeByRank) pipelineCmd).getStart(), ((PipelineZremRangeByRank) pipelineCmd).getEnd()));
                        break;
                    case ZREM_RANGE_BY_SCORE:
                        futures.add(pipeline.zremrangebyscore(((PipelineZremRangeByScore) pipelineCmd).getKey(), ((PipelineZremRangeByScore) pipelineCmd).getMin(), ((PipelineZremRangeByScore) pipelineCmd).getMax()));
                        break;
                    case ZREVRANGE:
                        futures.add(pipeline.zrevrange(((PipelineZrevRange) pipelineCmd).getKey(), ((PipelineZrevRange) pipelineCmd).getStartIndex(), ((PipelineZrevRange) pipelineCmd).getEndIndex()));
                        break;
                    default:
                        CacheExceptionFactory.addErrorLog("Error pipeline cmd: " + JSON.toJSONString(pipelineCmd));
                }
            }
            pipeline.flushCommands();
            return futures;
        } catch (Exception e) {
            CacheExceptionFactory.throwException("Jedis pipelined error ！", e);
        }
        CacheExceptionFactory.throwException("Jedis pipelined futuresource error ！");
        return null;
    }
}
