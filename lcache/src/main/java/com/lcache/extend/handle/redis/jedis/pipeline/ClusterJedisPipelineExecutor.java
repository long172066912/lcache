package com.lcache.extend.handle.redis.jedis.pipeline;

import com.lcache.exception.CacheExceptionFactory;
import com.lcache.extend.handle.pipeline.*;
import com.lcache.extend.handle.redis.jedis.JedisClusterPipeline;
import com.lcache.util.JsonUtil;
import redis.clients.jedis.JedisCluster;

import java.util.List;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: JedisPipelineExecutor
 * @Description: Jedis普通模式管道实现
 * @date 2021/2/3 11:43 AM
 */
public class ClusterJedisPipelineExecutor {
    /**
     * 普通方式管道
     */
    private JedisClusterPipeline pipeline;

    public ClusterJedisPipelineExecutor(JedisCluster jedisCluster) {
        this.pipeline = new JedisClusterPipeline(jedisCluster);
    }

    public List<Object> pSync(List<PipelineCmd> commands) {
        try {
            pipeline.refreshCluster();
            for (PipelineCmd pipelineCmd : commands) {
                if (pipelineCmd == null || pipelineCmd.getCmd() == null) {
                    continue;
                }
                switch (pipelineCmd.getCmd()) {
                    case SET:
                        if (((PipelineSet) pipelineCmd).getTime() < 0) {
                            pipeline.set(((PipelineSet) pipelineCmd).getKey(), ((PipelineSet) pipelineCmd).getValue());
                        } else {
                            pipeline.setex(((PipelineSet) pipelineCmd).getKey(), ((PipelineSet) pipelineCmd).getTime(), ((PipelineSet) pipelineCmd).getValue());
                        }
                        break;
                    case GET:
                        pipeline.get(((PipelineGet) pipelineCmd).getKey());
                        break;
                    case DEL:
                        String[] keys = ((PipelineDel) pipelineCmd).getKeys();
                        for (String key : keys) {
                            pipeline.del(key);
                        }
                        break;
                    case EXPIRE:
                        pipeline.expire(((PipelineExpire) pipelineCmd).getKey(), ((PipelineExpire) pipelineCmd).getTime());
                        break;
                    case SADD:
                        pipeline.sadd(((PipelineSAdd) pipelineCmd).getKey(), ((PipelineSAdd) pipelineCmd).getMembers());
                        break;
                    case SREM:
                        pipeline.srem(((PipelineSRem) pipelineCmd).getKey(), ((PipelineSRem) pipelineCmd).getMembers());
                        break;
                    case SISMEMBER:
                        pipeline.sismember(((PipelineSIsmember) pipelineCmd).getKey(), ((PipelineSIsmember) pipelineCmd).getMember());
                        break;
                    case SCARD:
                        pipeline.scard(((PipelineScard) pipelineCmd).getKey());
                        break;
                    case HSET:
                        pipeline.hset(((PipelineHSet) pipelineCmd).getKey(), ((PipelineHSet) pipelineCmd).getField(), ((PipelineHSet) pipelineCmd).getValue());
                        break;
                    case HDEL:
                        pipeline.hdel(((PipelineHDel) pipelineCmd).getKey(), ((PipelineHDel) pipelineCmd).getField());
                        break;
                    case HMSET:
                        pipeline.hmset(((PipelineHMSet) pipelineCmd).getKey(), ((PipelineHMSet) pipelineCmd).getValues());
                        break;
                    case HGET:
                        pipeline.hget(((PipelineHGet) pipelineCmd).getKey(), ((PipelineHGet) pipelineCmd).getField());
                        break;
                    case HGETALL:
                        pipeline.hgetAll(((PipelineHGetAll) pipelineCmd).getKey());
                        break;
                    case HINCRBY:
                        pipeline.hincrBy(((PipelineHincrBy) pipelineCmd).getKey(), ((PipelineHincrBy) pipelineCmd).getField(), ((PipelineHincrBy) pipelineCmd).getValue());
                        break;
                    case ZADD:
                        pipeline.zadd(((PipelineZadd) pipelineCmd).getKey(), ((PipelineZadd) pipelineCmd).getScore(), ((PipelineZadd) pipelineCmd).getMember());
                        break;
                    case ZADDBATCH:
                        pipeline.zadd(((PipelineZaddBatch) pipelineCmd).getKey(), ((PipelineZaddBatch) pipelineCmd).getScoreMembers());
                        break;
                    case ZINCRBY:
                        pipeline.zincrby(((PipelineZincrby) pipelineCmd).getKey(), ((PipelineZincrby) pipelineCmd).getCount(), ((PipelineZincrby) pipelineCmd).getMember());
                        break;
                    case ZINCRBYDOUBLE:
                        pipeline.zincrby(((PipelineZincrbyDouble) pipelineCmd).getKey(), ((PipelineZincrbyDouble) pipelineCmd).getCount(), ((PipelineZincrbyDouble) pipelineCmd).getMember());
                        break;
                    case ZREM:
                        pipeline.zrem(((PipelineZREM) pipelineCmd).getKey(), ((PipelineZREM) pipelineCmd).getMember());
                        break;
                    case ZSCORE:
                        pipeline.zscore(((PipelineZScore) pipelineCmd).getKey(), ((PipelineZScore) pipelineCmd).getMember());
                        break;
                    case PTTL:
                        pipeline.pttl(((PipelinePTtl) pipelineCmd).getKey());
                        break;
                    case LPUSH:
                        pipeline.lpush(((PipelineLPush) pipelineCmd).getKey(), ((PipelineLPush) pipelineCmd).getFields());
                        break;
                    case RPUSH:
                        pipeline.rpush(((PipelineRPush) pipelineCmd).getKey(), ((PipelineRPush) pipelineCmd).getFields());
                        break;
                    case LSET:
                        pipeline.lset(((PipelineLSet) pipelineCmd).getKey(), ((PipelineLSet) pipelineCmd).getIndex(), ((PipelineLSet) pipelineCmd).getValue());
                        break;
                    case INCR:
                        pipeline.incrBy(((PipelineIncr) pipelineCmd).getKey(), ((PipelineIncr) pipelineCmd).getIncrCount());
                        break;
                    case HEXISTS:
                        pipeline.hexists(((PipelineHExists) pipelineCmd).getKey(), ((PipelineHExists) pipelineCmd).getField());
                        break;
                    case HMGET:
                        pipeline.hmget(((PipelineHMGet) pipelineCmd).getKey(), ((PipelineHMGet) pipelineCmd).getFields());
                        break;
                    case ZRANGE_WITH_SCORES:
                        pipeline.zrangeWithScores(((PipelineZrangeWithScores) pipelineCmd).getKey(), ((PipelineZrangeWithScores) pipelineCmd).getStart(), ((PipelineZrangeWithScores) pipelineCmd).getEnd());
                        break;
                    case ZCARD:
                        pipeline.zcard(((PipelineZcard) pipelineCmd).getKey());
                        break;
                    case EXPIREAT:
                        pipeline.expireAt(((PipelineExpireAt) pipelineCmd).getKey(), ((PipelineExpireAt) pipelineCmd).getUnixTime());
                        break;
                    case ZREM_RANGE_BY_RANK:
                        pipeline.zremrangeByRank(((PipelineZremRangeByRank) pipelineCmd).getKey(), ((PipelineZremRangeByRank) pipelineCmd).getStart(), ((PipelineZremRangeByRank) pipelineCmd).getEnd());
                        break;
                    case ZREM_RANGE_BY_SCORE:
                        pipeline.zremrangeByScore(((PipelineZremRangeByScore) pipelineCmd).getKey(), ((PipelineZremRangeByScore) pipelineCmd).getMin(), ((PipelineZremRangeByScore) pipelineCmd).getMax());
                        break;
                    case ZREVRANGE:
                        pipeline.zrevrange(((PipelineZrevRange) pipelineCmd).getKey(), ((PipelineZrevRange) pipelineCmd).getStartIndex(), ((PipelineZrevRange) pipelineCmd).getEndIndex());
                        break;
                    default:
                        CacheExceptionFactory.addErrorLog("Error pipeline cmd: " + JsonUtil.toJSONString(pipelineCmd));
                }
            }
            return pipeline.syncAndReturnAll();
        } catch (Exception e) {
            CacheExceptionFactory.throwException("Jedis pipelined error ！", e);
        } finally {
            if (pipeline != null) {
                try {
                    pipeline.close();
                } catch (Exception e) {
                    CacheExceptionFactory.throwException("Jedis pipelined close fail ！", e);
                }
            }
        }
        CacheExceptionFactory.throwException("Jedis pipelined resource error ！");
        return null;
    }
}
