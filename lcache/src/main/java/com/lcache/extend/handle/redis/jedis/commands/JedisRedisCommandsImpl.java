package com.lcache.extend.handle.redis.jedis.commands;

import com.alibaba.fastjson2.JSON;
import com.lcache.core.cache.redis.lua.RedisLuaScripts;
import com.lcache.core.constant.RedisMagicConstants;
import com.lcache.core.constant.UseTypeEnum;
import com.lcache.core.model.InterfacePubSubModel;
import com.lcache.exception.CacheExceptionFactory;
import com.lcache.extend.handle.redis.jedis.AbstractJedisHandleExecutor;
import com.lcache.util.CacheCommonUtils;
import io.lettuce.core.*;
import org.apache.commons.collections4.CollectionUtils;
import redis.clients.jedis.*;
import redis.clients.jedis.params.geo.GeoRadiusParam;
import redis.clients.jedis.params.sortedset.ZAddParams;
import redis.clients.jedis.params.sortedset.ZIncrByParams;
import redis.clients.util.Slowlog;

import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: JedisRedisCommandsImpl
 * @Description: Jedis命令实现
 * @date 2021/1/26 4:23 PM
 */
@SuppressWarnings("unchecked")
public class JedisRedisCommandsImpl extends AbstractJedisHandleExecutor {

    @Override
    public Boolean set(String key, String value, int timeout) {
        return (Boolean) this.execute(() -> RedisMagicConstants.OK.equals(this.getConnectResource().setex(key, timeout, value)), key);
    }

    @Override
    public Boolean set(String key, String value, String nxxx, String expx, long time) {
        return (Boolean) this.execute(() -> RedisMagicConstants.OK.equals(this.getConnectResource().set(key, value, nxxx, expx, time)), key);
    }

    @Override
    public String get(String key) {
        return (String) this.execute(() -> this.getConnectResource().get(key), key);
    }

    @Override
    public Boolean exists(String key) {
        return (Boolean) this.execute(() -> this.getConnectResource().exists(key), key);
    }

    @Override
    public Long del(String key) {
        return (Long) this.execute(() -> this.getConnectResource().del(key), key);
    }

    @Override
    public Boolean expire(String key, int seconds) {
        return (Boolean) this.execute(() -> {
            return this.getConnectResource().expire(key, seconds) > 0 ? true : false;
        }, key);
    }

    @Override
    public Boolean expireAt(String key, long unixTime) {
        return (Boolean) this.execute(() -> {
            return this.getConnectResource().expireAt(key, unixTime) > 0 ? true : false;
        }, key);
    }

    @Override
    public Long ttl(String key) {
        return (Long) this.execute(() -> {
            return this.getConnectResource().ttl(key);
        }, key);
    }

    @Override
    public String getSet(String key, String value) {
        return (String) this.execute(() -> {
            return this.getConnectResource().getSet(key, value);
        }, key);
    }

    @Override
    public Map<String, Object> mget(String... keys) {
        List<String> list = null;
        switch (this.getCacheConfigModel().getConnectTypeEnum()) {
            case SHARDED:
                CacheExceptionFactory.throwException("ShardedJedis->mget(String... keys) 暂不支持此命令");
                break;
            case CLUSTER:
                list = (List<String>) this.execute(() -> {
                    return ((JedisCluster) this.getConnectResource()).mget(keys);
                }, keys);
                break;
            default:
                list = (List<String>) this.execute(() -> {
                    return ((Jedis) this.getConnectResource()).mget(keys);
                }, keys);
                break;
        }
        if (CollectionUtils.isEmpty(list)) {
            return new HashMap<String, Object>(2);
        }
        Map<String, Object> map = new HashMap<>(keys.length);
        for (int i = 0; i < keys.length; i++) {
            map.put(keys[i], list.get(i));
        }
        return map;
    }

    @Override
    public Boolean setnx(String key, String value, int seconds) {
        return RedisMagicConstants.OK.equals(this.execute(() -> this.getConnectResource().set(key, value, RedisMagicConstants.UNX, RedisMagicConstants.EX, seconds), key));
    }

    @Override
    public String setex(String key, int seconds, String value) {
        return (String) this.execute(() -> {
            return this.getConnectResource().setex(key, seconds, value);
        }, key);
    }

    @Override
    public String mset(int seconds, String... keysvalues) {
        Map<String, Object> keyValues = CacheCommonUtils.stringsToMap(keysvalues);
        if (null == keyValues) {
            CacheExceptionFactory.throwException("Jedis->mset 参数错误");
            return null;
        }
        switch (this.getCacheConfigModel().getConnectTypeEnum()) {
            case SHARDED:
                CacheExceptionFactory.throwException("ShardedJedis->mset(String... keysvalues) 暂不支持此命令");
                return null;
            case CLUSTER:
                return (String) this.executeAndDelLocal(() -> {
                    return ((JedisCluster) this.getConnectResource()).mset(keysvalues);
                }, seconds, keyValues.keySet().toArray(new String[keyValues.keySet().size()]));
            default:
                return (String) this.executeAndDelLocal(() -> {
                    return ((Jedis) this.getConnectResource()).mset(keysvalues);
                }, seconds, keyValues.keySet().toArray(new String[keyValues.keySet().size()]));
        }
    }

    @Override
    public Boolean msetnx(int seconds, String... keysvalues) {
        Map<String, Object> keyValues = CacheCommonUtils.stringsToMap(keysvalues);
        if (null == keyValues) {
            CacheExceptionFactory.throwException("Jedis->msetnx 参数错误");
            return null;
        }
        switch (this.getCacheConfigModel().getConnectTypeEnum()) {
            case SHARDED:
                CacheExceptionFactory.throwException("ShardedJedis->msetnx(String... keysvalues) 暂不支持此命令");
                return null;
            case CLUSTER:
                return (Boolean) this.executeAndDelLocal(() -> {
                    return ((JedisCluster) this.getConnectResource()).msetnx(keysvalues) > 0 ? true : false;
                }, seconds, keyValues.keySet().toArray(new String[keyValues.keySet().size()]));
            default:
                return (Boolean) this.executeAndDelLocal(() -> {
                    return ((Jedis) this.getConnectResource()).msetnx(keysvalues) > 0 ? true : false;
                }, seconds, keyValues.keySet().toArray(new String[keyValues.keySet().size()]));
        }
    }

    @Override
    public Long decrBy(String key, long decrement, int seconds) {
        return (Long) this.execute(() -> this.getConnectResource().decrBy(key, decrement), seconds, key);
    }

    @Override
    public Long decr(String key, int seconds) {
        return (Long) this.execute(() -> this.getConnectResource().decr(key), seconds, key);
    }

    @Override
    public Long incrBy(String key, long increment, int seconds) {
        return (Long) this.execute(() -> this.getConnectResource().incrBy(key, increment), seconds, key);
    }

    @Override
    public Double incrByFloat(String key, double increment, int seconds) {
        return (Double) this.execute(() -> this.getConnectResource().incrByFloat(key, increment), seconds, key);
    }

    @Override
    public Long incr(String key, int seconds) {
        return (Long) this.execute(() -> this.getConnectResource().incr(key), seconds, key);
    }

    @Override
    public Long append(String key, String value) {
        return (Long) this.execute(() -> {
            return this.getConnectResource().append(key, value);
        }, key);
    }

    @Override
    public String substr(String key, int start, int end) {
        return (String) this.execute(() -> {
            return this.getConnectResource().substr(key, start, end);
        }, key, new Object[]{start, end});
    }

    @Override
    public Boolean hset(String key, String field, String value, int seconds) {
        return (Boolean) this.execute(() -> this.getConnectResource().hset(key, field, value) > 0 ? true : false, seconds, key);
    }

    @Override
    @Deprecated
    public Long hset(String key, Map<String, String> hash, int seconds) {
        CacheExceptionFactory.throwException("Jedis->hset(String key, Map<String, String> hash) 暂不支持此命令");
        return null;
    }

    @Override
    public String hget(String key, String field) {
        return (String) this.execute(() -> {
            return this.getConnectResource().hget(key, field);
        }, key, new Object[]{field});
    }

    @Override
    public Boolean hsetnx(String key, String field, String value, int seconds) {
        return (Boolean) this.execute(() -> this.getConnectResource().hsetnx(key, field, value) > 0 ? true : false, seconds, key);
    }

    @Override
    public String hmset(String key, Map<String, String> hash, int seconds) {
        return (String) this.execute(() -> this.getConnectResource().hmset(key, hash), seconds, key);
    }

    @Override
    public List<String> hmget(String key, String[] fields) {
        return (List<String>) this.execute(() -> {
            return this.getConnectResource().hmget(key, fields);
        }, key, fields);
    }

    @Override
    public Map<String, Object> hmgetToMap(String key, String[] fields) {
        return (Map<String, Object>) this.execute(() -> {
            List<String> list = this.getConnectResource().hmget(key, fields);
            if (CollectionUtils.isEmpty(list)) {
                return new HashMap<>(2);
            }
            Map<String, Object> res = new HashMap<>(list.size());
            for (int i = 0; i < list.size(); i++) {
                if (null != list.get(i)) {
                    res.put(fields[i], list.get(i));
                }
            }
            return res;
        }, key, fields);
    }

    @Override
    public Map<String, Object> hmgetToMapCanNull(String key, String[] fields) {
        return (Map<String, Object>) this.execute(() -> {
            List<String> list = this.getConnectResource().hmget(key, fields);
            if (CollectionUtils.isEmpty(list)) {
                return new HashMap<>(2);
            }
            Map<String, Object> res = new HashMap<>(list.size());
            for (int i = 0; i < list.size(); i++) {
                res.put(fields[i], list.get(i));
            }
            return res;
        }, key, fields);
    }

    @Override
    public Long hincrBy(String key, String field, long value, int seconds) {
        return (Long) this.execute(() -> this.getConnectResource().hincrBy(key, field, value), seconds, key);
    }

    @Override
    public Double hincrByFloat(String key, String field, double value, int seconds) {
        return (Double) this.execute(() -> this.getConnectResource().hincrByFloat(key, field, value), seconds, key);
    }

    @Override
    public Boolean hexists(String key, String field) {
        return (Boolean) this.execute(() -> {
            return this.getConnectResource().hexists(key, field);
        }, key, new Object[]{field});
    }

    @Override
    public Long hdel(String key, String field) {
        return (Long) this.execute(() -> {
            return this.getConnectResource().hdel(key, field);
        }, key);
    }

    @Override
    public Long hdel(String key, String[] fields) {
        return (Long) this.execute(() -> {
            return this.getConnectResource().hdel(key, fields);
        }, key);
    }

    @Override
    public Long hlen(String key) {
        return (Long) this.execute(() -> {
            return this.getConnectResource().hlen(key);
        }, key);
    }

    @Override
    public Set<String> hkeys(String key) {
        return (Set<String>) this.execute(() -> {
            return this.getConnectResource().hkeys(key);
        }, key);
    }

    @Override
    public List<String> hvals(String key) {
        return (List<String>) this.execute(() -> {
            return this.getConnectResource().hvals(key);
        }, key);
    }

    @Override
    public Map<String, String> hgetAll(String key) {
        return (Map<String, String>) this.execute(() -> {
            return this.getConnectResource().hgetAll(key);
        }, key);
    }

    @Override
    public Long rpush(String key, String string, int seconds) {
        return (Long) this.execute(() -> this.getConnectResource().rpush(key, string), seconds, key);
    }

    @Override
    public Long lpush(String key, String string, int seconds) {
        return (Long) this.execute(() -> this.getConnectResource().lpush(key, string), seconds, key);
    }

    @Override
    public Long rpush(String key, String[] strings, int seconds) {
        return (Long) this.execute(() -> this.getConnectResource().rpush(key, strings), seconds, key);
    }

    @Override
    public Long lpush(String key, String[] strings, int seconds) {
        return (Long) this.execute(() -> this.getConnectResource().lpush(key, strings), seconds, key);
    }

    @Override
    public Long llen(String key) {
        return (Long) this.execute(() -> {
            return this.getConnectResource().llen(key);
        }, key);
    }

    @Override
    public List<String> lrange(String key, long start, long stop) {
        return (List<String>) this.execute(() -> {
            return this.getConnectResource().lrange(key, start, stop);
        }, key);
    }

    @Override
    public String ltrim(String key, long start, long stop) {
        return (String) this.execute(() -> {
            return this.getConnectResource().ltrim(key, start, stop);
        }, key);
    }

    @Override
    public String lindex(String key, long index) {
        return (String) this.execute(() -> {
            return this.getConnectResource().lindex(key, index);
        }, key);
    }

    @Override
    public String lset(String key, long index, String value, int seconds) {
        return (String) this.execute(() -> this.getConnectResource().lset(key, index, value), seconds, key);
    }

    @Override
    public Long lrem(String key, long count, String value) {
        return (Long) this.execute(() -> {
            return this.getConnectResource().lrem(key, count, value);
        }, key);
    }

    @Override
    public String lpop(String key) {
        return (String) this.execute(() -> {
            return this.getConnectResource().lpop(key);
        }, key);
    }

    @Override
    public String rpop(String key) {
        return (String) this.execute(() -> {
            return this.getConnectResource().rpop(key);
        }, key);
    }

    @Override
    public String rpoplpush(String srckey, String dstkey, int seconds) {
        switch (this.getCacheConfigModel().getConnectTypeEnum()) {
            case SHARDED:
                CacheExceptionFactory.throwException("ShardedJedis->rpoplpush(String srckey, String dstkey) 暂不支持此命令");
                return null;
            case CLUSTER:
                return (String) this.execute(() -> {
                    return ((JedisCluster) this.getConnectResource()).rpoplpush(srckey, dstkey);
                }, seconds, srckey);
            default:
                return (String) this.execute(() -> {
                    return ((Jedis) this.getConnectResource()).rpoplpush(srckey, dstkey);
                }, seconds, srckey);
        }
    }

    @Override
    public Long sadd(String key, String member, int seconds) {
        return (Long) this.execute(() -> this.getConnectResource().sadd(key, member), seconds, key);
    }

    @Override
    public Long sadd(String key, String[] members, int seconds) {
        return (Long) this.execute(() -> this.getConnectResource().sadd(key, members), seconds, key);
    }

    @Override
    public Set<String> smembers(String key) {
        return (Set<String>) this.execute(() -> {
            return this.getConnectResource().smembers(key);
        }, key);
    }

    @Override
    public Long srem(String key, String member) {
        return (Long) this.execute(() -> {
            return this.getConnectResource().srem(key, member);
        }, key);
    }

    @Override
    public Long srem(String key, String[] members) {
        return (Long) this.execute(() -> {
            return this.getConnectResource().srem(key, members);
        }, key);
    }

    @Override
    public String spop(String key) {
        return (String) this.execute(() -> {
            return this.getConnectResource().spop(key);
        }, key);
    }

    @Override
    public Set<String> spop(String key, long count) {
        return (Set<String>) this.execute(() -> {
            return this.getConnectResource().spop(key, count);
        }, key);
    }

    @Override
    public Boolean smove(String srckey, String dstkey, String member) {
        switch (this.getCacheConfigModel().getConnectTypeEnum()) {
            case SHARDED:
                CacheExceptionFactory.throwException("ShardedJedis->smove(String srckey, String dstkey, String member) 暂不支持此命令");
                return null;
            case CLUSTER:
                return (Boolean) this.execute(() -> {
                    return ((JedisCluster) this.getConnectResource()).smove(srckey, dstkey, member) > 0 ? true : false;
                }, srckey);
            default:
                return (Boolean) this.execute(() -> {
                    return ((Jedis) this.getConnectResource()).smove(srckey, dstkey, member) > 0 ? true : false;
                }, srckey);
        }
    }

    @Override
    public Long scard(String key) {
        return (Long) this.execute(() -> {
            return this.getConnectResource().scard(key);
        }, key);
    }

    @Override
    public Boolean sismember(String key, String member) {
        return (Boolean) this.execute(() -> {
            return this.getConnectResource().sismember(key, member);
        }, key, new String[]{member});
    }

    @Override
    public Set<String> sinter(String... keys) {
        switch (this.getCacheConfigModel().getConnectTypeEnum()) {
            case SHARDED:
                CacheExceptionFactory.throwException("ShardedJedis->sinter(String... keys) 暂不支持此命令");
                return null;
            case CLUSTER:
                return (Set<String>) this.execute(() -> {
                    return ((JedisCluster) this.getConnectResource()).sinter(keys);
                }, keys);
            default:
                return (Set<String>) this.execute(() -> {
                    return ((Jedis) this.getConnectResource()).sinter(keys);
                }, keys);
        }
    }

    @Override
    public Long sinterstore(String dstkey, int seconds, String... keys) {
        switch (this.getCacheConfigModel().getConnectTypeEnum()) {
            case SHARDED:
                CacheExceptionFactory.throwException("ShardedJedis->sinter(String... keys) 暂不支持此命令");
                return null;
            case CLUSTER:
                return (Long) this.execute(() -> {
                    return ((JedisCluster) this.getConnectResource()).sinterstore(dstkey, keys);
                }, seconds, keys);
            default:
                return (Long) this.execute(() -> {
                    return ((Jedis) this.getConnectResource()).sinterstore(dstkey, keys);
                }, seconds, keys);
        }
    }

    @Override
    public Set<String> sunion(String... keys) {
        switch (this.getCacheConfigModel().getConnectTypeEnum()) {
            case SHARDED:
                CacheExceptionFactory.throwException("ShardedJedis->sunion(String... keys) 暂不支持此命令");
                return null;
            case CLUSTER:
                return (Set<String>) this.execute(() -> {
                    return ((JedisCluster) this.getConnectResource()).sunion(keys);
                }, keys);
            default:
                return (Set<String>) this.execute(() -> {
                    return ((Jedis) this.getConnectResource()).sunion(keys);
                }, keys);
        }
    }

    @Override
    public Long sunionstore(String dstkey, int seconds, String... keys) {
        switch (this.getCacheConfigModel().getConnectTypeEnum()) {
            case SHARDED:
                CacheExceptionFactory.throwException("ShardedJedis->sunionstore(String dstkey, String... keys) 暂不支持此命令");
                return null;
            case CLUSTER:
                return (Long) this.execute(() -> {
                    return ((JedisCluster) this.getConnectResource()).sunionstore(dstkey, keys);
                }, seconds, keys);
            default:
                return (Long) this.execute(() -> {
                    return ((Jedis) this.getConnectResource()).sunionstore(dstkey, keys);
                }, seconds, keys);
        }
    }

    @Override
    public Set<String> sdiff(String... keys) {
        switch (this.getCacheConfigModel().getConnectTypeEnum()) {
            case SHARDED:
                CacheExceptionFactory.throwException("ShardedJedis->sdiff(String... keys) 暂不支持此命令");
                return null;
            case CLUSTER:
                return (Set<String>) this.execute(() -> {
                    return ((JedisCluster) this.getConnectResource()).sdiff(keys);
                }, keys);
            default:
                return (Set<String>) this.execute(() -> {
                    return ((Jedis) this.getConnectResource()).sdiff(keys);
                }, keys);
        }
    }

    @Override
    public Long sdiffstore(String dstkey, int seconds, String... keys) {
        switch (this.getCacheConfigModel().getConnectTypeEnum()) {
            case SHARDED:
                CacheExceptionFactory.throwException("ShardedJedis->sdiffstore(String dstkey, String... keys) 暂不支持此命令");
                return null;
            case CLUSTER:
                return (Long) this.execute(() -> {
                    return ((JedisCluster) this.getConnectResource()).sdiffstore(dstkey, keys);
                }, seconds, keys);
            default:
                return (Long) this.execute(() -> {
                    return ((Jedis) this.getConnectResource()).sdiffstore(dstkey, keys);
                }, seconds, keys);
        }
    }

    @Override
    public String srandmember(String key) {
        return (String) this.execute(() -> {
            return this.getConnectResource().srandmember(key);
        }, key);
    }

    @Override
    public List<String> srandmember(String key, int count) {
        return (List<String>) this.execute(() -> {
            return this.getConnectResource().srandmember(key, count);
        }, key);
    }

    @Override
    public Long zadd(String key, double score, String member, int seconds) {
        return (Long) this.execute(() -> this.getConnectResource().zadd(key, score, member), seconds, key);
    }

    @Override
    public Long zaddIfKeyExists(String key, double score, String member, int seconds) {
        try {
            return Long.parseLong(this.evalsha(this.getLuaSha1(RedisLuaScripts.ZADD_IF_EXISTS), ScriptOutputType.INTEGER, Arrays.asList(key), Arrays.asList(score + "", member)).toString());
        } finally {
            if (seconds > 0) {
                CompletableFuture.runAsync(() -> this.expire(key, seconds));
            }
        }
    }

    @Override
    public Long zadd(String key, double score, String member, ZAddParams params, int seconds) {
        return (Long) this.execute(() -> this.getConnectResource().zadd(key, score, member, params), key);
    }

    @Override
    public Long zadd(String key, Map<String, Double> scoreMembers, int seconds) {
        return (Long) this.execute(() -> this.getConnectResource().zadd(key, scoreMembers), seconds, key);
    }

    @Override
    public Long zadd(String key, Map<String, Double> scoreMembers, ZAddParams params, int seconds) {
        return (Long) this.execute(() -> this.getConnectResource().zadd(key, scoreMembers, params), seconds, key);
    }

    @Override
    public Set<String> zrange(String key, long start, long stop) {
        return (Set<String>) this.execute(() -> {
            return this.getConnectResource().zrange(key, start, stop);
        }, key);
    }

    @Override
    public Long zrem(String key, String member) {
        return (Long) this.execute(() -> {
            return this.getConnectResource().zrem(key, member);
        }, key);
    }

    @Override
    public Long zrem(String key, String[] members) {
        return (Long) this.execute(() -> {
            return this.getConnectResource().zrem(key, members);
        }, key);
    }

    @Override
    public Double zincrby(String key, double increment, String member, int seconds) {
        return (Double) this.execute(() -> this.getConnectResource().zincrby(key, increment, member), seconds, key);
    }

    @Override
    public Double zincrby(String key, double increment, String member, ZIncrByParams params, int seconds) {
        return (Double) this.execute(() -> this.getConnectResource().zincrby(key, increment, member, params), seconds, key);
    }

    @Override
    public Long zrank(String key, String member) {
        return (Long) this.execute(() -> {
            return this.getConnectResource().zrank(key, member);
        }, key);
    }

    @Override
    public Long zrevrank(String key, String member) {
        return (Long) this.execute(() -> {
            return this.getConnectResource().zrevrank(key, member);
        }, key);
    }

    @Override
    public Set<String> zrevrange(String key, long start, long stop) {
        return (Set<String>) this.execute(() -> {
            return this.getConnectResource().zrevrange(key, start, stop);
        }, key);
    }

    @Override
    public Set<Tuple> zrangeWithScores(String key, long start, long stop) {
        return (Set<Tuple>) this.execute(() -> {
            return this.getConnectResource().zrangeWithScores(key, start, stop);
        }, key);
    }

    @Override
    public Set<Tuple> zrevrangeWithScores(String key, long start, long stop) {
        return (Set<Tuple>) this.execute(() -> {
            return this.getConnectResource().zrevrangeWithScores(key, start, stop);
        }, key);
    }

    @Override
    public Long zcard(String key) {
        return (Long) this.execute(() -> {
            return this.getConnectResource().zcard(key);
        }, key);
    }

    @Override
    public Double zscore(String key, String member) {
        return (Double) this.execute(() -> {
            return this.getConnectResource().zscore(key, member);
        }, key);
    }

    @Override
    public Map<String, Double> zscoreBatch(String key, List<String> members) {
        return JSON.parseObject(this.execute(() -> this.evalsha(this.getLuaSha1(RedisLuaScripts.ZSCORE_BATCH), ScriptOutputType.VALUE, Arrays.asList(key), members), key).toString(), Map.class);
    }

    @Override
    @Deprecated
    public ScoredValue zpopmax(String key) {
        CacheExceptionFactory.throwException("Jedis->zpopmax(String key) 暂不支持此命令");
        return null;
    }

    @Override
    @Deprecated
    public List<ScoredValue> zpopmax(String key, int count) {
        CacheExceptionFactory.throwException("Jedis->zpopmax(String key, int count) 暂不支持此命令");
        return null;
    }

    @Override
    @Deprecated
    public ScoredValue zpopmin(String key) {
        CacheExceptionFactory.throwException("Jedis->zpopmin(String key) 暂不支持此命令");
        return null;
    }

    @Override
    @Deprecated
    public List<ScoredValue> zpopmin(String key, int count) {
        CacheExceptionFactory.throwException("Jedis->zpopmin(String key, int count) 暂不支持此命令");
        return null;
    }

    @Override
    @Deprecated
    public KeyValue<String, ScoredValue> bzpopmax(long timeout, String... keys) {
        CacheExceptionFactory.throwException("Jedis->bzpopmax(long timeout, String... keys) 暂不支持此命令");
        return null;
    }

    @Override
    @Deprecated
    public KeyValue<String, ScoredValue> bzpopmin(long timeout, String... keys) {
        CacheExceptionFactory.throwException("Jedis->bzpopmin(long timeout, String... keys) 暂不支持此命令");
        return null;
    }

    @Override
    public List<String> sort(String key) {
        return (List<String>) this.execute(() -> {
            return this.getConnectResource().sort(key);
        }, key);
    }

    @Override
    public List<String> sort(String key, SortingParams sortingParameters) {
        return (List<String>) this.execute(() -> {
            return this.getConnectResource().sort(key, sortingParameters);
        }, key);
    }

    @Override
    public List<String> blpop(int timeout, String... keys) {
        switch (this.getCacheConfigModel().getConnectTypeEnum()) {
            case SHARDED:
                CacheExceptionFactory.throwException("ShardedJedis->blpop(int timeout, String... keys) 暂不支持此命令");
                return null;
            case CLUSTER:
                return (List<String>) this.execute(() -> {
                    return ((JedisCluster) this.getConnectResource()).blpop(timeout, keys);
                }, keys);
            default:
                return (List<String>) this.execute(() -> {
                    return ((Jedis) this.getConnectResource()).blpop(timeout, keys);
                }, keys);
        }
    }

    @Override
    public Long sort(String key, SortingParams sortingParameters, String dstkey) {
        switch (this.getCacheConfigModel().getConnectTypeEnum()) {
            case SHARDED:
                CacheExceptionFactory.throwException("ShardedJedis->sort(String key, SortingParams sortingParameters, String dstkey) 暂不支持此命令");
                return null;
            case CLUSTER:
                return (Long) this.execute(() -> {
                    return ((JedisCluster) this.getConnectResource()).sort(key, sortingParameters, dstkey);
                }, key);
            default:
                return (Long) this.execute(() -> {
                    return ((Jedis) this.getConnectResource()).sort(key, sortingParameters, dstkey);
                }, key);
        }
    }

    @Override
    public Long sort(String key, String dstkey) {
        switch (this.getCacheConfigModel().getConnectTypeEnum()) {
            case SHARDED:
                CacheExceptionFactory.throwException("ShardedJedis->sort(String key, String dstkey) 暂不支持此命令");
                return null;
            case CLUSTER:
                return (Long) this.execute(() -> {
                    return ((JedisCluster) this.getConnectResource()).sort(key, dstkey);
                }, key);
            default:
                return (Long) this.execute(() -> {
                    return ((Jedis) this.getConnectResource()).sort(key, dstkey);
                }, key);
        }
    }

    @Override
    public List<String> brpop(int timeout, String[] keys) {
        switch (this.getCacheConfigModel().getConnectTypeEnum()) {
            case SHARDED:
                CacheExceptionFactory.throwException("ShardedJedis->brpop(int timeout, String... keys) 暂不支持此命令");
                return null;
            case CLUSTER:
                return (List<String>) this.execute(() -> {
                    return ((JedisCluster) this.getConnectResource()).brpop(timeout, keys);
                }, keys);
            default:
                return (List<String>) this.execute(() -> {
                    return ((Jedis) this.getConnectResource()).brpop(timeout, keys);
                }, keys);
        }
    }

    @Override
    public Long zcount(String key, double min, double max) {
        return (Long) this.execute(() -> {
            return this.getConnectResource().zcount(key, min, max);
        }, key);
    }

    @Override
    public Long zcount(String key, String min, String max) {
        return (Long) this.execute(() -> {
            return this.getConnectResource().zcount(key, min, max);
        }, key);
    }

    @Override
    public Set<String> zrangeByScore(String key, double min, double max) {
        return (Set<String>) this.execute(() -> {
            return this.getConnectResource().zrangeByScore(key, min, max);
        }, key);
    }

    @Override
    public Set<String> zrangeByScore(String key, String min, String max) {
        return (Set<String>) this.execute(() -> {
            return this.getConnectResource().zrangeByScore(key, min, max);
        }, key);
    }

    @Override
    public Set<String> zrangeByScore(String key, double min, double max, int offset, int count) {
        return (Set<String>) this.execute(() -> {
            return this.getConnectResource().zrangeByScore(key, min, max, offset, count);
        }, key);
    }

    @Override
    public Set<String> zrangeByScore(String key, String min, String max, int offset, int count) {
        return (Set<String>) this.execute(() -> {
            return this.getConnectResource().zrangeByScore(key, min, max, offset, count);
        }, key);
    }

    @Override
    public Set<Tuple> zrangeByScoreWithScores(String key, double min, double max) {
        return (Set<Tuple>) this.execute(() -> {
            return this.getConnectResource().zrangeByScoreWithScores(key, min, max);
        }, key);
    }

    @Override
    public Set<Tuple> zrangeByScoreWithScores(String key, String min, String max) {
        return (Set<Tuple>) this.execute(() -> {
            return this.getConnectResource().zrangeByScoreWithScores(key, min, max);
        }, key);
    }

    @Override
    public Set<Tuple> zrangeByScoreWithScores(String key, double min, double max, int offset, int count) {
        return (Set<Tuple>) this.execute(() -> {
            return this.getConnectResource().zrangeByScoreWithScores(key, min, max, offset, count);
        }, key);
    }

    @Override
    public Set<Tuple> zrangeByScoreWithScores(String key, String min, String max, int offset, int count) {
        return (Set<Tuple>) this.execute(() -> {
            return this.getConnectResource().zrangeByScoreWithScores(key, min, max, offset, count);
        }, key);
    }

    @Override
    public Set<String> zrevrangeByScore(String key, double max, double min) {
        return (Set<String>) this.execute(() -> {
            return this.getConnectResource().zrevrangeByScore(key, max, min);
        }, key);
    }

    @Override
    public Set<String> zrevrangeByScore(String key, String max, String min) {
        return (Set<String>) this.execute(() -> {
            return this.getConnectResource().zrevrangeByScore(key, max, min);
        }, key);
    }

    @Override
    public Set<String> zrevrangeByScore(String key, double max, double min, int offset, int count) {
        return (Set<String>) this.execute(() -> {
            return this.getConnectResource().zrevrangeByScore(key, max, min, offset, count);
        }, key);
    }

    @Override
    public Set<Tuple> zrevrangeByScoreWithScores(String key, double max, double min) {
        return (Set<Tuple>) this.execute(() -> {
            return this.getConnectResource().zrevrangeByScoreWithScores(key, max, min);
        }, key);
    }

    @Override
    public Set<Tuple> zrevrangeByScoreWithScores(String key, double max, double min, int offset, int count) {
        return (Set<Tuple>) this.execute(() -> {
            return this.getConnectResource().zrevrangeByScoreWithScores(key, max, min, offset, count);
        }, key);
    }

    @Override
    public Set<Tuple> zrevrangeByScoreWithScores(String key, String max, String min, int offset, int count) {
        return (Set<Tuple>) this.execute(() -> {
            return this.getConnectResource().zrevrangeByScoreWithScores(key, max, min, offset, count);
        }, key);
    }

    @Override
    public Set<String> zrevrangeByScore(String key, String max, String min, int offset, int count) {
        return (Set<String>) this.execute(() -> {
            return this.getConnectResource().zrevrangeByScore(key, max, min, offset, count);
        }, key);
    }

    @Override
    public Set<Tuple> zrevrangeByScoreWithScores(String key, String max, String min) {
        return (Set<Tuple>) this.execute(() -> {
            return this.getConnectResource().zrevrangeByScoreWithScores(key, max, min);
        }, key);
    }

    @Override
    public Long zremrangeByRank(String key, long start, long stop) {
        return (Long) this.execute(() -> {
            return this.getConnectResource().zremrangeByRank(key, start, stop);
        }, key);
    }

    @Override
    public Long zremrangeByScore(String key, double min, double max) {
        return (Long) this.execute(() -> {
            return this.getConnectResource().zremrangeByScore(key, min, max);
        }, key);
    }

    @Override
    public Long zremrangeByScore(String key, String min, String max) {
        return (Long) this.execute(() -> {
            return this.getConnectResource().zremrangeByScore(key, min, max);
        }, key);
    }

    @Override
    public Long zunionstore(String dstkey, String... sets) {
        switch (this.getCacheConfigModel().getConnectTypeEnum()) {
            case SHARDED:
                CacheExceptionFactory.throwException("ShardedJedis->zunionstore(String dstkey, String... sets) 暂不支持此命令");
                return null;
            case CLUSTER:
                return (Long) this.execute(() -> {
                    return ((JedisCluster) this.getConnectResource()).zunionstore(dstkey, sets);
                }, sets);
            default:
                return (Long) this.execute(() -> {
                    return ((Jedis) this.getConnectResource()).zunionstore(dstkey, sets);
                }, sets);
        }
    }

    @Override
    public Long zunionstore(String dstkey, ZParams params, String... sets) {
        switch (this.getCacheConfigModel().getConnectTypeEnum()) {
            case SHARDED:
                CacheExceptionFactory.throwException("ShardedJedis->zunionstore(String dstkey, ZParams params, String... sets) 暂不支持此命令");
                return null;
            case CLUSTER:
                return (Long) this.execute(() -> {
                    return ((JedisCluster) this.getConnectResource()).zunionstore(dstkey, params, sets);
                }, sets);
            default:
                return (Long) this.execute(() -> {
                    return ((Jedis) this.getConnectResource()).zunionstore(dstkey, params, sets);
                }, sets);
        }
    }

    @Override
    public Long zinterstore(String dstkey, String... sets) {
        switch (this.getCacheConfigModel().getConnectTypeEnum()) {
            case SHARDED:
                CacheExceptionFactory.throwException("ShardedJedis->zinterstore(String dstkey, String... sets) 暂不支持此命令");
                return null;
            case CLUSTER:
                return (Long) this.execute(() -> {
                    return ((JedisCluster) this.getConnectResource()).zinterstore(dstkey, sets);
                }, sets);
            default:
                return (Long) this.execute(() -> {
                    return ((Jedis) this.getConnectResource()).zinterstore(dstkey, sets);
                }, sets);
        }
    }

    @Override
    public Long zinterstore(String dstkey, ZParams params, String... sets) {
        switch (this.getCacheConfigModel().getConnectTypeEnum()) {
            case SHARDED:
                CacheExceptionFactory.throwException("ShardedJedis->zinterstore(String dstkey, ZParams params, String... sets) 暂不支持此命令");
                return null;
            case CLUSTER:
                return (Long) this.execute(() -> {
                    return ((JedisCluster) this.getConnectResource()).zinterstore(dstkey, params, sets);
                }, sets);
            default:
                return (Long) this.execute(() -> {
                    return ((Jedis) this.getConnectResource()).zinterstore(dstkey, params, sets);
                }, sets);
        }
    }

    @Override
    public Long zlexcount(String key, String min, String max) {
        return (Long) this.execute(() -> {
            return this.getConnectResource().zlexcount(key, min, max);
        }, key);
    }

    @Override
    public Set<String> zrangeByLex(String key, String min, String max) {
        return (Set<String>) this.execute(() -> {
            return this.getConnectResource().zrangeByLex(key, min, max);
        }, key);
    }

    @Override
    public Set<String> zrangeByLex(String key, String min, String max, int offset, int count) {
        return (Set<String>) this.execute(() -> {
            return this.getConnectResource().zrangeByLex(key, min, max, offset, count);
        }, key);
    }

    @Override
    public Set<String> zrevrangeByLex(String key, String max, String min) {
        return (Set<String>) this.execute(() -> {
            return this.getConnectResource().zrevrangeByLex(key, max, min);
        }, key);
    }

    @Override
    public Set<String> zrevrangeByLex(String key, String max, String min, int offset, int count) {
        return (Set<String>) this.execute(() -> {
            return this.getConnectResource().zrevrangeByLex(key, max, min, offset, count);
        }, key);
    }

    @Override
    public Long zremrangeByLex(String key, String min, String max) {
        return (Long) this.execute(() -> {
            return this.getConnectResource().zremrangeByLex(key, min, max);
        }, key);
    }

    @Override
    public Long strlen(String key) {
        return (Long) this.execute(() -> {
            return this.getConnectResource().strlen(key);
        }, key);
    }

    @Override
    public Long lpushx(String key, String... string) {
        return (Long) this.execute(() -> {
            return this.getConnectResource().lpushx(key, string);
        }, key);
    }

    @Override
    public Long rpushx(String key, String... string) {
        return (Long) this.execute(() -> {
            return this.getConnectResource().rpushx(key, string);
        }, key);
    }

    @Override
    public String echo(String string) {
        return (String) this.execute(() -> {
            return this.getConnectResource().echo(string);
        }, string);
    }

    @Override
    public Long linsert(String key, Client.LIST_POSITION where, String pivot, String value, int seconds) {
        return (Long) this.execute(() -> this.getConnectResource().linsert(key, where, pivot, value), seconds, key);
    }

    @Override
    public String brpoplpush(String source, String destination, int timeout) {
        switch (this.getCacheConfigModel().getConnectTypeEnum()) {
            case SHARDED:
                CacheExceptionFactory.throwException("ShardedJedis->brpoplpush(String source, String destination, int timeout) 暂不支持此命令");
                return null;
            case CLUSTER:
                return (String) this.execute(() -> {
                    return ((JedisCluster) this.getConnectResource()).brpoplpush(source, destination, timeout);
                }, source);
            default:
                return (String) this.execute(() -> {
                    return ((Jedis) this.getConnectResource()).brpoplpush(source, destination, timeout);
                }, source);
        }
    }

    @Override
    public Boolean setbit(String key, long offset, boolean value, int seconds) {
        return (Boolean) this.execute(() -> this.getConnectResource().setbit(key, offset, value), seconds, key);
    }

    @Override
    public Boolean setbit(String key, long offset, String value, int seconds) {
        return (Boolean) this.execute(() -> this.getConnectResource().setbit(key, offset, value), seconds, key);
    }

    @Override
    public Boolean getbit(String key, long offset) {
        return (Boolean) this.execute(() -> {
            return this.getConnectResource().getbit(key, offset);
        }, key);
    }

    @Override
    public Long setrange(String key, long offset, String value) {
        return (Long) this.execute(() -> {
            return this.getConnectResource().setrange(key, offset, value);
        }, key);
    }

    @Override
    public String getrange(String key, long startOffset, long endOffset) {
        return (String) this.execute(() -> {
            return this.getConnectResource().getrange(key, startOffset, endOffset);
        }, key);
    }

    @Override
    public Long bitpos(String key, boolean value) {
        return (Long) this.execute(() -> {
            return this.getConnectResource().bitpos(key, value);
        }, key);
    }

    @Override
    public Long bitpos(String key, boolean state, long start, long end) {
        return (Long) this.execute(() -> {
            return this.getConnectResource().bitpos(key, state, new BitPosParams(start, end));
        }, key);
    }

    @Override
    public void subscribe(JedisPubSub jedisPubSub, String[] channels) {
        switch (this.getCacheConfigModel().getConnectTypeEnum()) {
            case SHARDED:
                CacheExceptionFactory.throwException("ShardedJedis->subscribe(JedisPubSub jedisPubSub, String... channels) 暂不支持此命令");
                break;
            case CLUSTER:
                this.execute(() -> {
                    this.getCacheConfigModel().setUseType(UseTypeEnum.PUBSUB);
                    ((JedisCluster) this.getConnectResource()).subscribe(jedisPubSub, channels);
                    CacheExceptionFactory.throwException("JedisCluster->subscribe become invalid !");
                    return null;
                }, channels);
                break;
            default:
                this.execute(() -> {
                    this.getCacheConfigModel().setUseType(UseTypeEnum.PUBSUB);
                    ((Jedis) this.getConnectResource()).subscribe(jedisPubSub, channels);
                    CacheExceptionFactory.throwException("Jedis->subscribe become invalid !");
                    return null;
                }, channels);
                break;
        }
    }

    @Override
    public void subscribe(JedisPubSub jedisPubSub, String channel) {
        this.subscribe(jedisPubSub, new String[]{channel});
    }

    @Override
    public void subscribe(InterfacePubSubModel pubSubModel, String channel) {
        this.subscribe(pubSubModel, new String[]{channel});
    }

    @Override
    public void subscribe(InterfacePubSubModel pubSubModel, String[] channels) {
        switch (this.getCacheConfigModel().getConnectTypeEnum()) {
            case SHARDED:
                CacheExceptionFactory.throwException("ShardedJedis->subscribe(JedisPubSub jedisPubSub, String... channels) 暂不支持此命令");
                break;
            case CLUSTER:
                this.execute(() -> {
                    this.getCacheConfigModel().setUseType(UseTypeEnum.PUBSUB);
                    ((JedisCluster) this.getConnectResource()).subscribe(new JedisPubSub() {
                        @Override
                        public void onMessage(String channel, String message) {
                            pubSubModel.onMessage(message);
                        }
                    }, channels);
                    CacheExceptionFactory.throwException("JedisCluster->subscribe become invalid !");
                    return null;
                }, channels);
                break;
            default:
                this.execute(() -> {
                    this.getCacheConfigModel().setUseType(UseTypeEnum.PUBSUB);
                    ((Jedis) this.getConnectResource()).subscribe(new JedisPubSub() {
                        @Override
                        public void onMessage(String channel, String message) {
                            pubSubModel.onMessage(message);
                        }
                    }, channels);
                    CacheExceptionFactory.throwException("Jedis->subscribe become invalid !");
                    return null;
                }, channels);
                break;
        }
    }

    @Override
    public Long publish(String channel, String message) {
        switch (this.getCacheConfigModel().getConnectTypeEnum()) {
            case SHARDED:
                CacheExceptionFactory.throwException("ShardedJedis->publish(String channel, String message) 暂不支持此命令");
                return null;
            case CLUSTER:
                return (Long) this.execute(() -> {
                    return ((JedisCluster) this.getConnectResource()).publish(channel, message);
                }, channel);
            default:
                return (Long) this.execute(() -> {
                    return ((Jedis) this.getConnectResource()).publish(channel, message);
                }, channel);
        }
    }

    @Override
    public void publishAsync(String channel, String message) {
        CompletableFuture.runAsync(() -> this.publish(channel, message));
    }

    @Override
    public void psubscribe(JedisPubSub jedisPubSub, String... patterns) {
        switch (this.getCacheConfigModel().getConnectTypeEnum()) {
            case SHARDED:
                CacheExceptionFactory.throwException("ShardedJedis->psubscribe(JedisPubSub jedisPubSub, String... patterns) 暂不支持此命令");
                break;
            case CLUSTER:
                this.execute(() -> {
                    this.getCacheConfigModel().setUseType(UseTypeEnum.PUBSUB);
                    ((JedisCluster) this.getConnectResource()).psubscribe(jedisPubSub, patterns);
                    CacheExceptionFactory.throwException("JedisCluster->psubscribe become invalid !");
                    return null;
                }, "");
                break;
            default:
                this.execute(() -> {
                    this.getCacheConfigModel().setUseType(UseTypeEnum.PUBSUB);
                    ((Jedis) this.getConnectResource()).psubscribe(jedisPubSub, patterns);
                    CacheExceptionFactory.throwException("Jedis->psubscribe become invalid !");
                    return null;
                }, "");
                break;
        }
    }

    @Override
    public List<Slowlog> slowlogGet() {
        switch (this.getCacheConfigModel().getConnectTypeEnum()) {
            case SHARDED:
                CacheExceptionFactory.throwException("ShardedJedis->slowlogGet() 暂不支持此命令");
                return null;
            case CLUSTER:
                CacheExceptionFactory.throwException("JedisCluster->slowlogGet() 暂不支持此命令");
                return null;
            default:
                return (List<Slowlog>) this.execute(() -> {
                    return ((Jedis) this.getConnectResource()).slowlogGet();
                }, "");
        }
    }

    @Override
    public List<Slowlog> slowlogGet(long entries) {
        switch (this.getCacheConfigModel().getConnectTypeEnum()) {
            case SHARDED:
                CacheExceptionFactory.throwException("ShardedJedis->slowlogGet(long entries) 暂不支持此命令");
                return null;
            case CLUSTER:
                CacheExceptionFactory.throwException("JedisCluster->slowlogGet(long entries) 暂不支持此命令");
                return null;
            default:
                return (List<Slowlog>) this.execute(() -> {
                    return ((Jedis) this.getConnectResource()).slowlogGet(entries);
                }, "");
        }
    }

    @Override
    public Long bitcount(String key) {
        return (Long) this.execute(() -> {
            return this.getConnectResource().bitcount(key);
        }, key);
    }

    @Override
    public Long bitcount(String key, long start, long end) {
        return (Long) this.execute(() -> {
            return this.getConnectResource().bitcount(key, start, end);
        }, key, new Object[]{start, end});
    }

    @Override
    public Long bitop(BitOP op, String destKey, String... srcKeys) {
        switch (this.getCacheConfigModel().getConnectTypeEnum()) {
            case SHARDED:
                CacheExceptionFactory.throwException("ShardedJedis->bitop(BitOP op, String destKey, String... srcKeys) 暂不支持此命令");
                return null;
            case CLUSTER:
                return (Long) this.execute(() -> {
                    return ((JedisCluster) this.getConnectResource()).bitop(op, destKey, srcKeys);
                }, srcKeys);
            default:
                return (Long) this.execute(() -> {
                    return ((Jedis) this.getConnectResource()).bitop(op, destKey, srcKeys);
                }, srcKeys);
        }
    }

    @Override
    public Boolean pexpire(String key, long milliseconds) {
        return (Boolean) this.execute(() -> {
            return this.getConnectResource().pexpire(key, milliseconds) > 0 ? true : false;
        }, key);
    }

    @Override
    public Boolean pexpireAt(String key, long millisecondsTimestamp) {
        return (Boolean) this.execute(() -> {
            return this.getConnectResource().pexpireAt(key, millisecondsTimestamp) > 0 ? true : false;
        }, key);
    }

    @Override
    public Long pttl(String key) {
        return (Long) this.execute(() -> {
            return this.getConnectResource().pttl(key);
        }, key);
    }

    @Override
    public String psetex(String key, long milliseconds, String value) {
        return (String) this.execute(() -> {
            return this.getConnectResource().psetex(key, milliseconds, value);
        }, key);
    }

    @Override
    public ScanResult<String> scan(String cursor) {
        switch (this.getCacheConfigModel().getConnectTypeEnum()) {
            case SHARDED:
                CacheExceptionFactory.throwException("ShardedJedis->scan(String cursor) 暂不支持此命令");
                return null;
            case CLUSTER:
                CacheExceptionFactory.throwException("JedisCluster->scan(String cursor) 暂不支持此命令 only supports SCAN commands with non-empty MATCH patterns");
                return null;
            default:
                return (ScanResult<String>) this.execute(() -> {
                    return ((Jedis) this.getConnectResource()).scan(cursor);
                }, "");
        }
    }

    @Override
    public ScanResult<String> scan(String cursor, ScanParams params) {
        switch (this.getCacheConfigModel().getConnectTypeEnum()) {
            case SHARDED:
                CacheExceptionFactory.throwException("ShardedJedis->scan(String cursor) 暂不支持此命令");
                return null;
            case CLUSTER:
                return (ScanResult<String>) this.execute(() -> {
                    return ((JedisCluster) this.getConnectResource()).scan(cursor, params);
                }, "");
            default:
                return (ScanResult<String>) this.execute(() -> {
                    return ((Jedis) this.getConnectResource()).scan(cursor, params);
                }, "");
        }
    }

    @Override
    public ScanResult<Map.Entry<String, String>> hscan(String key, String cursor) {
        return (ScanResult<Map.Entry<String, String>>) this.execute(() -> {
            return this.getConnectResource().hscan(key, cursor);
        }, key);
    }

    @Override
    public ScanResult<Map.Entry<String, String>> hscan(String key, String cursor, ScanParams params) {
        return (ScanResult<Map.Entry<String, String>>) this.execute(() -> {
            return this.getConnectResource().hscan(key, cursor, params);
        }, key);
    }

    @Override
    public ScanResult<String> sscan(String key, String cursor) {
        return (ScanResult<String>) this.execute(() -> {
            return this.getConnectResource().sscan(key, cursor);
        }, key);
    }

    @Override
    public ScanResult<String> sscan(String key, String cursor, ScanParams params) {
        return (ScanResult<String>) this.execute(() -> {
            return this.getConnectResource().sscan(key, cursor, params);
        }, key);
    }

    @Override
    public ScanResult<Tuple> zscan(String key, String cursor) {
        return (ScanResult<Tuple>) this.execute(() -> {
            return this.getConnectResource().zscan(key, cursor);
        }, key);
    }

    @Override
    public ScanResult<Tuple> zscan(String key, String cursor, ScanParams params) {
        return (ScanResult<Tuple>) this.execute(() -> {
            return this.getConnectResource().zscan(key, cursor, params);
        }, key);
    }

    @Override
    public List<String> pubsubChannels(String pattern) {
        switch (this.getCacheConfigModel().getConnectTypeEnum()) {
            case SHARDED:
                CacheExceptionFactory.throwException("ShardedJedis->pubsubChannels(String pattern) 暂不支持此命令");
                return null;
            case CLUSTER:
                CacheExceptionFactory.throwException("JedisCluster->pubsubChannels(String pattern) 暂不支持此命令");
                return null;
            default:
                return (List<String>) this.execute(() -> {
                    return ((Jedis) this.getConnectResource()).pubsubChannels(pattern);
                }, "");
        }
    }

    @Override
    public Long pubsubNumPat() {
        switch (this.getCacheConfigModel().getConnectTypeEnum()) {
            case SHARDED:
                CacheExceptionFactory.throwException("ShardedJedis->pubsubNumPat() 暂不支持此命令");
                return null;
            case CLUSTER:
                CacheExceptionFactory.throwException("JedisCluster->pubsubNumPat() 暂不支持此命令");
                return null;
            default:
                return (Long) this.execute(() -> {
                    return ((Jedis) this.getConnectResource()).pubsubNumPat();
                }, "");
        }
    }

    @Override
    public Map<String, String> pubsubNumSub(String... channels) {
        switch (this.getCacheConfigModel().getConnectTypeEnum()) {
            case SHARDED:
                CacheExceptionFactory.throwException("ShardedJedis->pubsubNumSub(String... channels) 暂不支持此命令");
                return null;
            case CLUSTER:
                CacheExceptionFactory.throwException("JedisCluster->pubsubNumSub(String... channels) 暂不支持此命令");
                return null;
            default:
                return (Map<String, String>) this.execute(() -> {
                    return ((Jedis) this.getConnectResource()).pubsubNumSub(channels);
                }, channels);
        }
    }

    @Override
    public Long pfadd(String key, int seconds, String... elements) {
        return (Long) this.execute(() -> this.getConnectResource().pfadd(key, elements), seconds, key);
    }

    @Override
    public long pfcount(String key) {
        return (Long) this.execute(() -> {
            return this.getConnectResource().pfcount(key);
        }, key);
    }

    @Override
    public long pfcount(String... keys) {
        switch (this.getCacheConfigModel().getConnectTypeEnum()) {
            case SHARDED:
                CacheExceptionFactory.throwException("ShardedJedis->pfcount(String... keys) 暂不支持此命令");
                return 0;
            case CLUSTER:
                return (long) this.execute(() -> {
                    return ((JedisCluster) this.getConnectResource()).pfcount(keys);
                }, keys);
            default:
                return (long) this.execute(() -> {
                    return ((Jedis) this.getConnectResource()).pfcount(keys);
                }, keys);
        }
    }

    @Override
    public String pfmerge(String destkey, int seconds, String... sourcekeys) {
        switch (this.getCacheConfigModel().getConnectTypeEnum()) {
            case SHARDED:
                CacheExceptionFactory.throwException("ShardedJedis->pfmerge(String destkey, String... sourcekeys) 暂不支持此命令");
                return null;
            case CLUSTER:
                return (String) this.execute(() -> {
                    return ((JedisCluster) this.getConnectResource()).pfmerge(destkey, sourcekeys);
                }, seconds, sourcekeys);
            default:
                return (String) this.execute(() -> {
                    return ((Jedis) this.getConnectResource()).pfmerge(destkey, sourcekeys);
                }, seconds, sourcekeys);
        }
    }

    @Override
    public List<String> blpop(int timeout, String key) {
        return (List<String>) this.execute(() -> {
            return this.getConnectResource().blpop(timeout, key);
        }, key);
    }

    @Override
    public List<String> brpop(int timeout, String key) {
        return (List<String>) this.execute(() -> {
            return this.getConnectResource().brpop(timeout, key);
        }, key);
    }

    @Override
    public Long geoadd(String key, double longitude, double latitude, String member, int seconds) {
        return (Long) this.execute(() -> this.getConnectResource().geoadd(key, longitude, latitude, member), seconds, key);
    }

    @Override
    public Long geoadd(String key, Map<String, GeoCoordinate> memberCoordinateMap, int seconds) {
        return (Long) this.execute(() -> this.getConnectResource().geoadd(key, memberCoordinateMap), seconds, key);
    }

    @Override
    public Double geodist(String key, String member1, String member2) {
        return (Double) this.execute(() -> {
            return this.getConnectResource().geodist(key, member1, member2);
        }, key);
    }

    @Override
    public Double geodist(String key, String member1, String member2, GeoUnit unit) {
        return (Double) this.execute(() -> {
            return this.getConnectResource().geodist(key, member1, member2, unit);
        }, key);
    }

    @Override
    public List<String> geohash(String key, String... members) {
        return (List<String>) this.execute(() -> {
            return this.getConnectResource().geohash(key, members);
        }, key);
    }

    @Override
    public List<GeoCoordinate> geopos(String key, String... members) {
        return (List<GeoCoordinate>) this.execute(() -> {
            return this.getConnectResource().geopos(key, members);
        }, key);
    }

//    @Override
//    public List<GeoRadiusResponse> georadius(String key, double longitude, double latitude, double radius, GeoUnit unit) {
//        return (List<GeoRadiusResponse>) this.execute(() -> {
//            return this.getConnectResource().georadius(key, longitude, latitude, radius, unit);
//        },Thread.currentThread() .getStackTrace()[1].getMethodName(), key);
//    }

    @Override
    public List<GeoRadiusResponse> georadius(String key, double longitude, double latitude, double radius, GeoUnit unit, GeoRadiusParam param) {
        return (List<GeoRadiusResponse>) this.execute(() -> {
            return this.getConnectResource().georadius(key, longitude, latitude, radius, unit, param);
        }, key);
    }

//    @Override
//    public List<GeoRadiusResponse> georadiusByMember(String key, String member, double radius, GeoUnit unit) {
//        return (List<GeoRadiusResponse>) this.execute(() -> {
//            return this.getConnectResource().georadiusByMember(key, member, radius, unit);
//        },Thread.currentThread() .getStackTrace()[1].getMethodName(), key);
//    }

    @Override
    public List<GeoRadiusResponse> georadiusByMember(String key, String member, double radius, GeoUnit unit, GeoRadiusParam param) {
        return (List<GeoRadiusResponse>) this.execute(() -> {
            return this.getConnectResource().georadiusByMember(key, member, radius, unit, param);
        }, key);
    }

    @Override
    public List<Long> bitfield(String key, String... arguments) {
        return (List<Long>) this.execute(() -> {
            return this.getConnectResource().bitfield(key, arguments);
        }, key);
    }

    @Override
    public Long hstrlen(String key, String field) {
        CacheExceptionFactory.throwException("Jedis->hstrlen(String key, String field) 暂不支持此命令");
        return null;
    }

    @Override
    public List<String> sort(String key, SortArgs sortArgs) {
        CacheExceptionFactory.throwException("Jedis->sort(String key, SortArgs sortArgs) 暂不支持此命令，请使用Lettuce");
        return null;
    }

    @Override
    public Long sort(String key, SortArgs sortArgs, String dstkey) {
        CacheExceptionFactory.throwException("Jedis->sort(String key, SortArgs sortArgs, String dstkey) 暂不支持此命令，请使用Lettuce");
        return null;
    }

    @Override
    public List<Long> bitfield(String key, BitFieldArgs bitFieldArgs) {
        CacheExceptionFactory.throwException("Jedis->bitfield(String key, BitFieldArgs bitFieldArgs) 暂不支持此命令，请使用Lettuce");
        return null;
    }

    @Override
    public Object eval(String script, ScriptOutputType outputType, int keyCount, String[] params) {
        switch (this.getCacheConfigModel().getConnectTypeEnum()) {
            case SHARDED:
                CacheExceptionFactory.throwException("ShardedJedis->eval(String script, int keyCount, String... params) 暂不支持此命令");
                return null;
            case CLUSTER:
                return this.execute(() -> {
                    return ((JedisCluster) this.getConnectResource()).eval(script, keyCount, params);
                }, "");
            default:
                return this.execute(() -> {
                    return ((Jedis) this.getConnectResource()).eval(script, keyCount, params);
                }, "");
        }
    }

    @Override
    public Object eval(String script, ScriptOutputType outputType, List<String> keys, List<String> args) {
        switch (this.getCacheConfigModel().getConnectTypeEnum()) {
            case SHARDED:
                CacheExceptionFactory.throwException("ShardedJedis->eval(String script, List<String> keys, List<String> args) 暂不支持此命令");
                return null;
            case CLUSTER:
                return this.execute(() -> {
                    return ((JedisCluster) this.getConnectResource()).eval(script, keys, args);
                }, "");
            default:
                return this.execute(() -> {
                    return ((Jedis) this.getConnectResource()).eval(script, keys, args);
                }, "");
        }
    }

    @Override
    public Object eval(String script, ScriptOutputType outputType) {
        switch (this.getCacheConfigModel().getConnectTypeEnum()) {
            case SHARDED:
                CacheExceptionFactory.throwException("ShardedJedis->eval(String script) 暂不支持此命令");
                return null;
            case CLUSTER:
                CacheExceptionFactory.throwException("JedisCluster->eval(String script) 暂不支持此命令");
                return null;
            default:
                return this.execute(() -> {
                    return ((Jedis) this.getConnectResource()).eval(script);
                }, "");
        }
    }

    @Override
    public Object evalsha(String sha1, ScriptOutputType outputType) {
        switch (this.getCacheConfigModel().getConnectTypeEnum()) {
            case SHARDED:
                CacheExceptionFactory.throwException("ShardedJedis->evalsha(String sha1) 暂不支持此命令");
                return null;
            case CLUSTER:
                CacheExceptionFactory.throwException("JedisCluster->evalsha(String sha1) 暂不支持此命令");
                return null;
            default:
                return this.execute(() -> {
                    return ((Jedis) this.getConnectResource()).evalsha(sha1);
                }, "");
        }
    }

    @Override
    public Object evalsha(String sha1, ScriptOutputType outputType, List<String> keys, List<String> args) {
        switch (this.getCacheConfigModel().getConnectTypeEnum()) {
            case SHARDED:
                CacheExceptionFactory.throwException("ShardedJedis->evalsha(String sha1, List<String> keys, List<String> args) 暂不支持此命令");
                return null;
            case CLUSTER:
                return this.execute(() -> {
                    return ((JedisCluster) this.getConnectResource()).evalsha(sha1, keys, args);
                }, "");
            default:
                return this.execute(() -> {
                    return ((Jedis) this.getConnectResource()).evalsha(sha1, keys, args);
                }, "");
        }
    }

    @Override
    public Object evalsha(String sha1, ScriptOutputType outputType, int keyCount, String[] params) {
        switch (this.getCacheConfigModel().getConnectTypeEnum()) {
            case SHARDED:
                CacheExceptionFactory.throwException("ShardedJedis->evalsha(String sha1, int keyCount, String... params) 暂不支持此命令");
                return null;
            case CLUSTER:
                return this.execute(() -> {
                    return ((JedisCluster) this.getConnectResource()).evalsha(sha1, keyCount, params);
                }, "");
            default:
                return this.execute(() -> {
                    return ((Jedis) this.getConnectResource()).evalsha(sha1, keyCount, params);
                }, "");
        }
    }

    @Override
    public Boolean scriptExists(String sha1) {
        switch (this.getCacheConfigModel().getConnectTypeEnum()) {
            case SHARDED:
                CacheExceptionFactory.throwException("ShardedJedis->scriptExists(String sha1) 暂不支持此命令");
                return null;
            case CLUSTER:
                return (Boolean) this.execute(() -> {
                    return ((JedisCluster) this.getConnectResource()).scriptExists("", sha1);
                }, "");
            default:
                return (Boolean) this.execute(() -> {
                    return ((Jedis) this.getConnectResource()).scriptExists(sha1);
                }, "");
        }
    }

    @Override
    public List<Boolean> scriptExists(String... sha1) {
        switch (this.getCacheConfigModel().getConnectTypeEnum()) {
            case SHARDED:
                CacheExceptionFactory.throwException("ShardedJedis->scriptExists(String sha1) 暂不支持此命令");
                return null;
            case CLUSTER:
                return (List<Boolean>) this.execute(() -> {
                    return ((JedisCluster) this.getConnectResource()).scriptExists("", sha1);
                }, "");
            default:
                return (List<Boolean>) this.execute(() -> {
                    return ((Jedis) this.getConnectResource()).scriptExists(sha1);
                }, "");
        }
    }

    @Override
    public String scriptLoad(String script) {
        switch (this.getCacheConfigModel().getConnectTypeEnum()) {
            case SHARDED:
                CacheExceptionFactory.throwException("ShardedJedis->scriptExists(String sha1) 暂不支持此命令");
                return null;
            case CLUSTER:
                return (String) this.execute(() -> {
                    return ((JedisCluster) this.getConnectResource()).scriptLoad(script, "");
                }, "");
            default:
                return (String) this.execute(() -> {
                    return ((Jedis) this.getConnectResource()).scriptLoad(script);
                }, "");
        }
    }
}
