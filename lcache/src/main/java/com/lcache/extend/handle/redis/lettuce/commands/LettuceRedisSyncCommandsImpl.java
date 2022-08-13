package com.lcache.extend.handle.redis.lettuce.commands;

import com.alibaba.fastjson.JSON;
import com.lcache.core.cache.redis.lua.RedisLuaScripts;
import com.lcache.core.constant.RedisMagicConstants;
import com.lcache.core.model.InterfacePubSubModel;
import com.lcache.exception.CacheExceptionFactory;
import com.lcache.extend.handle.redis.lettuce.AbstractLettuceHandleExecutor;
import com.lcache.util.CacheCommonUtils;
import io.lettuce.core.*;
import io.lettuce.core.pubsub.RedisPubSubListener;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import org.apache.commons.collections4.CollectionUtils;
import redis.clients.jedis.*;
import redis.clients.jedis.params.geo.GeoRadiusParam;
import redis.clients.jedis.params.sortedset.ZAddParams;
import redis.clients.jedis.params.sortedset.ZIncrByParams;
import redis.clients.util.SafeEncoder;
import redis.clients.util.Slowlog;

import java.nio.charset.Charset;
import java.util.*;

import static redis.clients.jedis.Protocol.Keyword.*;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: LettuceRedisSyncCommandsImpl
 * @Description: Lettuce同步命令实现
 * @date 2021/2/23 3:12 PM
 */
@SuppressWarnings("unchecked")
public class LettuceRedisSyncCommandsImpl extends AbstractLettuceHandleExecutor {

    @Override
    public Boolean set(String key, String value, int timeout) {
        return this.execute(() -> RedisMagicConstants.OK.equals(this.sync().setex(key, timeout, value)), key);
    }

    @Override
    public Boolean set(String key, String value, String nxxx, String expx, long time) {
        return this.execute(() -> {
            SetArgs setArgs = new SetArgs();
            if (RedisMagicConstants.UNX.equals(nxxx) || RedisMagicConstants.NX.equals(nxxx)) {
                setArgs.nx();
            }
            if (RedisMagicConstants.UXX.equals(nxxx) || RedisMagicConstants.XX.equals(nxxx)) {
                setArgs.xx();
            }
            if (RedisMagicConstants.UEX.equals(expx) || RedisMagicConstants.EX.equals(expx)) {
                setArgs.ex(time);
            }
            if (RedisMagicConstants.UPX.equals(expx) || RedisMagicConstants.PX.equals(expx)) {
                setArgs.px(time);
            }
            return RedisMagicConstants.OK.equals(this.sync().set(key, value, setArgs));
        }, key);
    }

    @Override
    public String get(String key) {
        return (String) this.execute(() -> this.sync().get(key), key);
    }

    @Override
    public Boolean exists(String key) {
        return this.execute(() -> this.sync().exists(key) > 0 ? true : false, key);
    }

    @Override
    public Long del(String key) {
        return this.execute(() -> this.sync().del(key), key);
    }

    @Override
    public Boolean expire(String key, int seconds) {
        return this.execute(() -> this.sync().expire(key, seconds), key);
    }

    @Override
    public Boolean expireAt(String key, long unixTime) {
        return this.execute(() -> this.sync().expireat(key, unixTime), key);
    }

    @Override
    public Long ttl(String key) {
        return this.execute(() -> this.sync().ttl(key), key);
    }

    @Override
    public String getSet(String key, String value) {
        return (String) this.execute(() -> this.sync().getset(key, value), key);
    }

    @Override
    public Map<String, Object> mget(String... keys) {
        List<KeyValue<String, Object>> list = (List<KeyValue<String, Object>>) this.execute(() -> {
            return this.sync().mget(keys);
        }, keys);
        if (CollectionUtils.isEmpty(list)) {
            return new HashMap<>(2);
        }
        Map<String, Object> map = new HashMap<>(list.size());
        for (KeyValue<String, Object> stringObjectKeyValue : list) {
            stringObjectKeyValue.ifHasValue(e -> {
                map.put(stringObjectKeyValue.getKey(), stringObjectKeyValue.getValue());
            });
            stringObjectKeyValue.ifEmpty(() -> {
                map.put(stringObjectKeyValue.getKey(), null);
            });
        }
        return map;
    }

    @Override
    public Boolean setnx(String key, String value, int seconds) {
        return this.set(key, value, RedisMagicConstants.UNX, RedisMagicConstants.EX, seconds);
    }

    @Override
    public String setex(String key, int seconds, String value) {
        return this.execute(() -> this.sync().setex(key, seconds, value), key);
    }

    @Override
    public String mset(int seconds, String... keysvalues) {
        Map<String, Object> keyValues = CacheCommonUtils.stringsToMap(keysvalues);
        if (null == keyValues) {
            CacheExceptionFactory.throwException("Lettuce->mset 参数错误");
            return null;
        }
        return this.executeAndDelLocal(() -> this.sync().mset(keyValues), seconds, keyValues.keySet().toArray(new String[keyValues.keySet().size()]));
    }

    @Override
    public Boolean msetnx(int seconds, String... keysvalues) {
        Map<String, Object> keyValues = CacheCommonUtils.stringsToMap(keysvalues);
        if (null == keyValues) {
            CacheExceptionFactory.throwException("Lettuce->msetnx 参数错误");
            return null;
        }
        return this.executeAndDelLocal(() -> this.sync().msetnx(keyValues), seconds, keyValues.keySet().toArray(new String[keyValues.keySet().size()]));
    }

    @Override
    public Long decrBy(String key, long decrement, int seconds) {
        return this.execute(() -> this.sync().decrby(key, decrement), seconds, key);
    }

    @Override
    public Long decr(String key, int seconds) {
        return (Long) this.execute(() -> this.sync().decr(key), seconds, key);
    }

    @Override
    public Long incrBy(String key, long increment, int seconds) {
        return this.execute(() -> this.sync().incrby(key, increment), seconds, key);
    }

    @Override
    public Double incrByFloat(String key, double increment, int seconds) {
        return this.execute(() -> this.sync().incrbyfloat(key, increment), seconds, key);
    }

    @Override
    public Long incr(String key, int seconds) {
        return this.execute(() -> this.sync().incr(key), seconds, key);
    }

    @Override
    public Long append(String key, String value) {
        return this.execute(() -> this.sync().append(key, value), key);
    }

    @Override
    public String substr(String key, int start, int end) {
        return (String) this.execute(() -> this.sync().getrange(key, start, end), key, new Object[]{start, end});
    }

    @Override
    public Boolean hset(String key, String field, String value, int seconds) {
        return this.execute(() -> this.sync().hset(key, field, value), seconds, key);
    }

    @Override
    public Long hset(String key, Map<String, String> hash, int seconds) {
        return this.execute(() -> RedisMagicConstants.OK.equals(this.sync().hmset(key, hash)) ? 1L : 0L, seconds, key);
    }

    @Override
    public String hget(String key, String field) {
        return (String) this.execute(() -> this.sync().hget(key, field), key, new Object[]{field});
    }

    @Override
    public Boolean hsetnx(String key, String field, String value, int seconds) {
        return this.execute(() -> this.sync().hsetnx(key, field, value), seconds, key);
    }

    @Override
    public String hmset(String key, Map<String, String> hash, int seconds) {
        return this.execute(() -> this.sync().hmset(key, hash), seconds, key);
    }

    @Override
    public List<String> hmget(String key, String[] fields) {
        return this.execute(() -> {
            List<KeyValue<String, String>> list = this.sync().hmget(key, fields);
            if (CollectionUtils.isEmpty(list)) {
                return new ArrayList<>();
            }
            List<String> res = new ArrayList<>();
            for (KeyValue<String, String> stringObjectKeyValue : list) {
                stringObjectKeyValue.ifHasValue(e -> {
                    res.add(stringObjectKeyValue.getValue());
                });
                stringObjectKeyValue.ifEmpty(() -> {
                    res.add(null);
                });
            }
            return res;
        }, key, fields);
    }

    @Override
    public Map<String, Object> hmgetToMap(String key, String[] fields) {
        return this.execute(() -> {
            List<KeyValue<String, Object>> list = this.sync().hmget(key, fields);
            if (CollectionUtils.isEmpty(list)) {
                return new HashMap<>(2);
            }
            Map<String, Object> res = new HashMap<>(list.size());
            for (KeyValue<String, Object> stringObjectKeyValue : list) {
                stringObjectKeyValue.ifHasValue(e -> {
                    res.put(stringObjectKeyValue.getKey(), stringObjectKeyValue.getValue());
                });
            }
            return res;
        }, key, fields);
    }

    @Override
    public Map<String, Object> hmgetToMapCanNull(String key, String[] fields) {
        return this.execute(() -> {
            List<KeyValue<String, Object>> list = this.sync().hmget(key, fields);
            if (CollectionUtils.isEmpty(list)) {
                return new HashMap<>(2);
            }
            Map<String, Object> res = new HashMap<>(list.size());
            for (KeyValue<String, Object> stringObjectKeyValue : list) {
                stringObjectKeyValue.ifHasValue(e -> {
                    res.put(stringObjectKeyValue.getKey(), stringObjectKeyValue.getValue());
                });
                stringObjectKeyValue.ifEmpty(() -> {
                    res.put(stringObjectKeyValue.getKey(), null);
                });
            }
            return res;
        }, key, fields);
    }

    @Override
    public Long hincrBy(String key, String field, long value, int seconds) {
        return this.execute(() -> this.sync().hincrby(key, field, value), seconds, key);
    }

    @Override
    public Double hincrByFloat(String key, String field, double value, int seconds) {
        return this.execute(() -> this.sync().hincrbyfloat(key, field, value), seconds, key);
    }

    @Override
    public Boolean hexists(String key, String field) {
        return this.execute(() -> {
            return this.sync().hexists(key, field);
        }, key, new Object[]{field});
    }

    @Override
    public Long hdel(String key, String field) {
        return this.execute(() -> {
            return this.sync().hdel(key, field);
        }, key);
    }

    @Override
    public Long hdel(String key, String[] fields) {
        return this.execute(() -> {
            return this.sync().hdel(key, fields);
        }, key);
    }

    @Override
    public Long hlen(String key) {
        return this.execute(() -> {
            return this.sync().hlen(key);
        }, key);
    }

    @Override
    public Set<String> hkeys(String key) {
        return (Set<String>) this.execute(() -> {
            List hkeys = this.sync().hkeys(key);
            if (CollectionUtils.isNotEmpty(hkeys)) {
                return new LinkedHashSet<>(hkeys);
            }
            return null;
        }, key);
    }

    @Override
    public List<String> hvals(String key) {
        return (List<String>) this.execute(() -> this.sync().hvals(key), key);
    }

    @Override
    public Map<String, String> hgetAll(String key) {
        return (Map<String, String>) this.execute(() -> this.sync().hgetall(key), key);
    }

    @Override
    public Long rpush(String key, String string, int seconds) {
        return this.execute(() -> this.sync().rpush(key, string), seconds, key);
    }

    @Override
    public Long lpush(String key, String string, int seconds) {
        return this.execute(() -> this.sync().lpush(key, string), key);
    }

    @Override
    public Long rpush(String key, String[] strings, int seconds) {
        return this.execute(() -> this.sync().rpush(key, strings), seconds, key);
    }

    @Override
    public Long lpush(String key, String[] strings, int seconds) {
        return this.execute(() -> this.sync().lpush(key, strings), seconds, key);
    }

    @Override
    public Long llen(String key) {
        return this.execute(() -> this.sync().llen(key), key);
    }

    @Override
    public List<String> lrange(String key, long start, long stop) {
        return (List<String>) this.execute(() -> this.sync().lrange(key, start, stop), key);
    }

    @Override
    public String ltrim(String key, long start, long stop) {
        return this.execute(() -> this.sync().ltrim(key, start, stop), key);
    }

    @Override
    public String lindex(String key, long index) {
        return (String) this.execute(() -> this.sync().lindex(key, index), key);
    }

    @Override
    public String lset(String key, long index, String value, int seconds) {
        return this.execute(() -> this.sync().lset(key, index, value), seconds, key);
    }

    @Override
    public Long lrem(String key, long count, String value) {
        return this.execute(() -> this.sync().lrem(key, count, value), key);
    }

    @Override
    public String lpop(String key) {
        return (String) this.execute(() -> this.sync().lpop(key), key);
    }

    @Override
    public String rpop(String key) {
        return (String) this.execute(() -> this.sync().rpop(key), key);
    }

    @Override
    public String rpoplpush(String srckey, String dstkey, int seconds) {
        return (String) this.execute(() -> this.sync().rpoplpush(srckey, dstkey), seconds, srckey);
    }

    @Override
    public Long sadd(String key, String member, int seconds) {
        return this.execute(() -> this.sync().sadd(key, member), seconds, key);
    }

    @Override
    public Long sadd(String key, String[] members, int seconds) {
        return this.execute(() -> this.sync().sadd(key, members), seconds, key);
    }

    @Override
    public Set<String> smembers(String key) {
        return (Set<String>) this.execute(() -> this.sync().smembers(key), key);
    }

    @Override
    public Long srem(String key, String member) {
        return this.execute(() -> this.sync().srem(key, member), key);
    }

    @Override
    public Long srem(String key, String[] members) {
        return this.execute(() -> this.sync().srem(key, members), key);
    }

    @Override
    public String spop(String key) {
        return (String) this.execute(() -> this.sync().spop(key), key);
    }

    @Override
    public Set<String> spop(String key, long count) {
        return (Set<String>) this.execute(() -> this.sync().spop(key, count), key);
    }

    @Override
    public Boolean smove(String srckey, String dstkey, String member) {
        return this.execute(() -> this.sync().smove(srckey, dstkey, member), srckey);
    }

    @Override
    public Long scard(String key) {
        return this.execute(() -> this.sync().scard(key), key);
    }

    @Override
    public Boolean sismember(String key, String member) {
        return this.execute(() -> this.sync().sismember(key, member), key, new String[]{member});
    }

    @Override
    public Set<String> sinter(String... keys) {
        return (Set<String>) this.execute(() -> this.sync().sinter(keys), keys);
    }

    @Override
    public Long sinterstore(String dstkey, int seconds, String... keys) {
        return this.execute(() -> this.sync().sinterstore(dstkey, keys), seconds, keys);
    }

    @Override
    public Set<String> sunion(String... keys) {
        return (Set<String>) this.execute(() -> this.sync().sunion(keys), keys);
    }

    @Override
    public Long sunionstore(String dstkey, int seconds, String... keys) {
        return this.execute(() -> this.sync().sunionstore(dstkey, keys), seconds, keys);
    }

    @Override
    public Set<String> sdiff(String... keys) {
        return (Set<String>) this.execute(() -> this.sync().sdiff(keys), keys);
    }

    @Override
    public Long sdiffstore(String dstkey, int seconds, String... keys) {
        return this.execute(() -> this.sync().sdiffstore(dstkey, keys), seconds, keys);
    }

    @Override
    public String srandmember(String key) {
        return (String) this.execute(() -> this.sync().srandmember(key), key);
    }

    @Override
    public List<String> srandmember(String key, int count) {
        return (List<String>) this.execute(() -> this.sync().srandmember(key, count), key);
    }

    @Override
    public Long zadd(String key, double score, String member, int seconds) {
        return this.execute(() -> this.sync().zadd(key, score, member), seconds, key);
    }

    @Override
    public Long zaddIfKeyExists(String key, double score, String member, int seconds) {
        return Long.parseLong(this.execute(() -> this.sync().evalsha(this.getLuaSha1(RedisLuaScripts.ZADD_IF_EXISTS), ScriptOutputType.VALUE, new String[]{key}, new String[]{score + "", member}), seconds, key).toString());
    }

    @Override
    public Long zadd(String key, double score, String member, ZAddParams params, int seconds) {
        return this.execute(() -> this.sync().zadd(key, this.zAddParamsToZaddArgs(params), score, member), seconds, key);
    }

    private ZAddArgs zAddParamsToZaddArgs(ZAddParams params) {
        ZAddArgs zAddArgs = new ZAddArgs();
        if (null != params) {
            if (null != params.getParam(RedisMagicConstants.NX)) {
                zAddArgs.nx();
            }
            if (null != params.getParam(RedisMagicConstants.XX)) {
                zAddArgs.xx();
            }
            if (null != params.getParam(RedisMagicConstants.CH)) {
                zAddArgs.ch();
            }
        }
        return zAddArgs;
    }

    private ZAddArgs zIncrByParamsToZaddArgs(ZIncrByParams params) {
        ZAddArgs zAddArgs = new ZAddArgs();
        if (null != params) {
            if (null != params.getParam(RedisMagicConstants.NX)) {
                zAddArgs.nx();
            }
            if (null != params.getParam(RedisMagicConstants.XX)) {
                zAddArgs.xx();
            }
            if (null != params.getParam(RedisMagicConstants.CH)) {
                zAddArgs.ch();
            }
        }
        return zAddArgs;
    }

    private Object[] scoreMembersToObjects(Map<String, Double> scoreMembers) {
        Object[] scoresAndValues = new Object[scoreMembers.size() * 2];
        int i = 0;
        for (Map.Entry<String, Double> m : scoreMembers.entrySet()) {
            scoresAndValues[i++] = m.getValue();
            scoresAndValues[i++] = m.getKey();
        }
        return scoresAndValues;
    }

    @Override
    public Long zadd(String key, Map<String, Double> scoreMembers, int seconds) {
        return this.execute(() -> this.sync().zadd(key, this.scoreMembersToObjects(scoreMembers)), seconds, key);
    }

    @Override
    public Long zadd(String key, Map<String, Double> scoreMembers, ZAddParams params, int seconds) {
        return this.execute(() -> this.sync().zadd(key, this.zAddParamsToZaddArgs(params), this.scoreMembersToObjects(scoreMembers)), seconds, key);
    }

    @Override
    public Set<String> zrange(String key, long start, long stop) {
        return (Set<String>) this.execute(() -> {
            List zrange = this.sync().zrange(key, start, stop);
            if (CollectionUtils.isNotEmpty(zrange)) {
                return new LinkedHashSet<>(zrange);
            }
            return null;
        }, key);
    }

    @Override
    public Long zrem(String key, String member) {
        return this.execute(() -> this.sync().zrem(key, member), key);
    }

    @Override
    public Long zrem(String key, String[] members) {
        return this.execute(() -> this.sync().zrem(key, members), key);
    }

    @Override
    public Double zincrby(String key, double increment, String member, int seconds) {
        return this.execute(() -> this.sync().zincrby(key, increment, member), seconds, key);
    }

    @Override
    public Double zincrby(String key, double increment, String member, ZIncrByParams params, int seconds) {
        return this.execute(() -> this.sync().zaddincr(key, this.zIncrByParamsToZaddArgs(params), increment, member), seconds, key);
    }

    @Override
    public Long zrank(String key, String member) {
        return this.execute(() -> this.sync().zrank(key, member), key);
    }

    @Override
    public Long zrevrank(String key, String member) {
        return this.execute(() -> this.sync().zrevrank(key, member), key);
    }

    @Override
    public Set<String> zrevrange(String key, long start, long stop) {
        return (Set<String>) this.execute(() -> {
            List zrevrange = this.sync().zrevrange(key, start, stop);
            if (CollectionUtils.isNotEmpty(zrevrange)) {
                return new LinkedHashSet<>(zrevrange);
            }
            return null;
        }, key);
    }

    /**
     * 类型转换
     *
     * @param list
     * @return
     */
    private Set<Tuple> scoreValuesToTuples(List<ScoredValue> list) {
        if (CollectionUtils.isNotEmpty(list)) {
            Set<Tuple> res = new LinkedHashSet<>();
            for (ScoredValue scoredValue : list) {
                if (null != scoredValue) {
                    res.add(new Tuple((String) scoredValue.getValue(), scoredValue.getScore()));
                }
            }
            return res;
        }
        return new LinkedHashSet<>();
    }

    private List<Tuple> scoreValuesToTupleList(List<ScoredValue> list) {
        if (CollectionUtils.isNotEmpty(list)) {
            List<Tuple> res = new ArrayList<>();
            for (ScoredValue scoredValue : list) {
                res.add(new Tuple((String) scoredValue.getValue(), scoredValue.getScore()));
            }
            return res;
        }
        return new ArrayList<>();
    }

    @Override
    public Set<Tuple> zrangeWithScores(String key, long start, long stop) {
        return (Set<Tuple>) this.execute(() -> this.scoreValuesToTuples(this.sync().zrangeWithScores(key, start, stop)), key);
    }

    @Override
    public Set<Tuple> zrevrangeWithScores(String key, long start, long stop) {
        return (Set<Tuple>) this.execute(() -> this.scoreValuesToTuples(this.sync().zrevrangeWithScores(key, start, stop)), key);
    }

    @Override
    public Long zcard(String key) {
        return this.execute(() -> this.sync().zcard(key), key);
    }

    @Override
    public Double zscore(String key, String member) {
        return this.execute(() -> this.sync().zscore(key, member), key);
    }

    @Override
    public Map<String, Double> zscoreBatch(String key, List<String> members) {
        return JSON.parseObject(this.execute(() -> this.sync().evalsha(this.getLuaSha1(RedisLuaScripts.ZSCORE_BATCH), ScriptOutputType.VALUE, new String[]{key}, members.toArray()), key).toString(), Map.class);
    }

    @Override
    public ScoredValue zpopmax(String key) {
        return this.execute(() -> this.sync().zpopmax(key), key);
    }

    @Override
    public List<ScoredValue> zpopmax(String key, int count) {
        return (List<ScoredValue>) this.execute(() -> this.sync().zpopmax(key, count), key);
    }

    @Override
    public ScoredValue zpopmin(String key) {
        return this.execute(() -> this.sync().zpopmin(key), key);
    }

    @Override
    public List<ScoredValue> zpopmin(String key, int count) {
        return (List<ScoredValue>) this.execute(() -> this.sync().zpopmin(key, count), key);
    }

    @Override
    public KeyValue<String, ScoredValue> bzpopmax(long timeout, String... keys) {
        return (KeyValue<String, ScoredValue>) this.execute(() -> this.sync().bzpopmax(timeout, keys), keys);
    }

    @Override
    public KeyValue<String, ScoredValue> bzpopmin(long timeout, String... keys) {
        return (KeyValue<String, ScoredValue>) this.execute(() -> this.sync().bzpopmin(timeout, keys), keys);
    }

    @Override
    public List<String> sort(String key) {
        return (List<String>) this.execute(() -> this.sync().sort(key), key);
    }

    @Override
    public List<String> sort(String key, SortingParams sortingParameters) {
        CacheExceptionFactory.throwException("Lettuce暂不支持此命令，请使用sort(String key, SortArgs sortArgs)");
        return null;
    }

    @Override
    public List<String> sort(String key, SortArgs sortArgs) {
        return (List<String>) this.execute(() -> this.sync().sort(key, sortArgs), key);
    }

    @Override
    public Long sort(String key, SortingParams sortingParameters, String dstkey) {
        CacheExceptionFactory.throwException("Lettuce暂不支持此命令，请使用sort(String key, SortArgs sortArgs, String dstkey)");
        return null;
    }

    @Override
    public Long sort(String key, SortArgs sortArgs, String dstkey) {
        return this.execute(() -> this.sync().sortStore(key, sortArgs, dstkey), key);
    }

    @Override
    public Long sort(String key, String dstkey) {
        return this.execute(() -> this.sync().sortStore(key, new SortArgs(), dstkey), key);
    }

    @Override
    public List<String> blpop(int timeout, String... keys) {
        return this.execute(() -> {
            KeyValue blpop = this.sync().blpop(timeout, keys);
            if (null != blpop) {
                List<String> res = new ArrayList<>();
                res.add((String) blpop.getValue());
                return res;
            }
            return null;
        }, keys);
    }

    @Override
    public List<String> brpop(int timeout, String[] keys) {
        return this.execute(() -> {
            KeyValue brpop = this.sync().brpop(timeout, keys);
            if (null != brpop) {
                List<String> res = new ArrayList<>();
                res.add((String) brpop.getValue());
                return res;
            }
            return null;
        }, keys);
    }

    @Override
    public Long zcount(String key, double min, double max) {
        return this.execute(() -> {
            return this.sync().zcount(key, min, max);
        }, key);
    }

    @Override
    public Long zcount(String key, String min, String max) {
        return this.execute(() -> {
            return this.sync().zcount(key, min, max);
        }, key);
    }

    @Override
    public Set<String> zrangeByScore(String key, double min, double max) {
        return (Set<String>) this.execute(() -> {
            List zrangebyscore = this.sync().zrangebyscore(key, min, max);
            if (CollectionUtils.isNotEmpty(zrangebyscore)) {
                return new LinkedHashSet<>(zrangebyscore);
            }
            return null;
        }, key);
    }

    @Override
    public Set<String> zrangeByScore(String key, String min, String max) {
        return (Set<String>) this.execute(() -> {
            List list = this.sync().zrangebyscore(key, min, max);
            if (CollectionUtils.isNotEmpty(list)) {
                return new LinkedHashSet<>(list);
            }
            return null;
        }, key);
    }

    @Override
    public Set<String> zrangeByScore(String key, double min, double max, int offset, int count) {
        return (Set<String>) this.execute(() -> {
            List zrangebyscore = this.sync().zrangebyscore(key, min, max, offset, count);
            if (CollectionUtils.isNotEmpty(zrangebyscore)) {
                return new LinkedHashSet<>(zrangebyscore);
            }
            return null;
        }, key);
    }

    @Override
    public Set<String> zrangeByScore(String key, String min, String max, int offset, int count) {
        return (Set<String>) this.execute(() -> {
            List zrangebyscore = this.sync().zrangebyscore(key, min, max, offset, count);
            if (CollectionUtils.isNotEmpty(zrangebyscore)) {
                return new LinkedHashSet<>(zrangebyscore);
            }
            return null;
        }, key);
    }

    @Override
    public Set<Tuple> zrangeByScoreWithScores(String key, double min, double max) {
        return (Set<Tuple>) this.execute(() -> this.scoreValuesToTuples(this.sync().zrangebyscoreWithScores(key, min, max)), key);
    }

    @Override
    public Set<Tuple> zrangeByScoreWithScores(String key, String min, String max) {
        return (Set<Tuple>) this.execute(() -> this.scoreValuesToTuples(this.sync().zrangebyscoreWithScores(key, min, max)), key);
    }

    @Override
    public Set<Tuple> zrangeByScoreWithScores(String key, double min, double max, int offset, int count) {
        return (Set<Tuple>) this.execute(() -> this.scoreValuesToTuples(this.sync().zrangebyscoreWithScores(key, min, max, offset, count)), key);
    }

    @Override
    public Set<Tuple> zrangeByScoreWithScores(String key, String min, String max, int offset, int count) {
        return (Set<Tuple>) this.execute(() -> this.scoreValuesToTuples(this.sync().zrangebyscoreWithScores(key, min, max, offset, count)), key);
    }

    @Override
    public Set<String> zrevrangeByScore(String key, double max, double min) {
        return (Set<String>) this.execute(() -> {
            List zrevrangebyscore = this.sync().zrevrangebyscore(key, max, min);
            if (CollectionUtils.isNotEmpty(zrevrangebyscore)) {
                return new HashSet<>(zrevrangebyscore);
            }
            return new LinkedHashSet<>();
        }, key);
    }

    @Override
    public Set<String> zrevrangeByScore(String key, String max, String min) {
        return (Set<String>) this.execute(() -> {
            List list = this.sync().zrevrangebyscore(key, min, max);
            if (CollectionUtils.isNotEmpty(list)) {
                return new LinkedHashSet<>(list);
            }
            return new LinkedHashSet<>();
        }, key);
    }

    @Override
    public Set<String> zrevrangeByScore(String key, double max, double min, int offset, int count) {
        return (Set<String>) this.execute(() -> {
            List list = this.sync().zrevrangebyscore(key, min, max, offset, count);
            if (CollectionUtils.isNotEmpty(list)) {
                return new LinkedHashSet<>(list);
            }
            return new LinkedHashSet<>();
        }, key);
    }

    @Override
    public Set<Tuple> zrevrangeByScoreWithScores(String key, double max, double min) {
        return (Set<Tuple>) this.execute(() -> this.scoreValuesToTuples(this.sync().zrevrangebyscoreWithScores(key, max, min)), key);
    }

    @Override
    public Set<Tuple> zrevrangeByScoreWithScores(String key, double max, double min, int offset, int count) {
        return (Set<Tuple>) this.execute(() -> this.scoreValuesToTuples(this.sync().zrevrangebyscoreWithScores(key, max, min, offset, count)), key);
    }

    @Override
    public Set<Tuple> zrevrangeByScoreWithScores(String key, String max, String min, int offset, int count) {
        return (Set<Tuple>) this.execute(() -> this.scoreValuesToTuples(this.sync().zrevrangebyscoreWithScores(key, max, min, offset, count)), key);
    }

    @Override
    public Set<String> zrevrangeByScore(String key, String max, String min, int offset, int count) {
        return (Set<String>) this.execute(() -> {
            List list = this.sync().zrevrangebyscore(key, max, min, offset, count);
            if (CollectionUtils.isNotEmpty(list)) {
                return new LinkedHashSet<>(list);
            }
            return new LinkedHashSet<>();
        }, key);
    }

    @Override
    public Set<Tuple> zrevrangeByScoreWithScores(String key, String max, String min) {
        return (Set<Tuple>) this.execute(() -> this.scoreValuesToTuples(this.sync().zrevrangebyscoreWithScores(key, max, min)), key);
    }

    @Override
    public Long zremrangeByRank(String key, long start, long stop) {
        return this.execute(() -> this.sync().zremrangebyrank(key, start, stop), key);
    }

    @Override
    public Long zremrangeByScore(String key, double min, double max) {
        return this.execute(() -> this.sync().zremrangebyscore(key, min, max), key);
    }

    @Override
    public Long zremrangeByScore(String key, String min, String max) {
        return this.execute(() -> this.sync().zremrangebyscore(key, min, max), key);
    }

    @Override
    public Long zunionstore(String dstkey, String... sets) {
        return this.execute(() -> this.sync().zunionstore(dstkey, sets), sets);
    }

    private ZStoreArgs zParamsToZstoreArgs(ZParams params) {
        ZStoreArgs zStoreArgs = new ZStoreArgs();
        Collection<byte[]> paramsList = params.getParams();
        if (CollectionUtils.isEmpty(paramsList)) {
            CacheExceptionFactory.throwException("ZParams to ZStoreArgs error ! ZParams is empty !");
            return null;
        }
        Map<byte[], byte[]> paramsMap = new HashMap<>(paramsList.size() >> 1);
        byte[] key = null;
        int i = 0;
        for (byte[] bytes : paramsList) {
            i++;
            if (i % 2 > 0) {
                key = bytes;
            } else {
                paramsMap.put(key, bytes);
            }
        }
        if (null != paramsMap.get(WEIGHTS.raw)) {
            zStoreArgs.weights(Double.valueOf(SafeEncoder.encode(paramsMap.get(WEIGHTS.raw))));
        }
        if (null != paramsMap.get(AGGREGATE.raw)) {
            ZParams.Aggregate aggregate = ZParams.Aggregate.valueOf(SafeEncoder.encode(paramsMap.get(AGGREGATE.raw)));
            switch (aggregate) {
                case MAX:
                    zStoreArgs.max();
                    break;
                case MIN:
                    zStoreArgs.min();
                    break;
                case SUM:
                    zStoreArgs.sum();
                    break;
                default:
                    break;
            }
        }
        return zStoreArgs;
    }

    @Override
    public Long zunionstore(String dstkey, ZParams params, String... sets) {
        return this.execute(() -> this.sync().zunionstore(dstkey, this.zParamsToZstoreArgs(params), sets), sets);
    }

    @Override
    public Long zinterstore(String dstkey, String... sets) {
        return this.execute(() -> this.sync().zinterstore(dstkey, sets), sets);
    }

    @Override
    public Long zinterstore(String dstkey, ZParams params, String... sets) {
        return this.execute(() -> this.sync().zinterstore(dstkey, this.zParamsToZstoreArgs(params), sets), sets);
    }

    @Override
    public Long zlexcount(String key, String min, String max) {
        return this.execute(() -> this.sync().zlexcount(key, min, max), key);
    }

    @Override
    public Set<String> zrangeByLex(String key, String min, String max) {
        return (Set<String>) this.execute(() -> {
            List list = this.sync().zrangebylex(key, min, max);
            if (CollectionUtils.isNotEmpty(list)) {
                return new LinkedHashSet<>(list);
            }
            return new LinkedHashSet<>();
        }, key);
    }

    @Override
    public Set<String> zrangeByLex(String key, String min, String max, int offset, int count) {
        return (Set<String>) this.execute(() -> {
            List list = this.sync().zrangebylex(key, min, max, offset, count);
            if (CollectionUtils.isNotEmpty(list)) {
                return new LinkedHashSet<>(list);
            }
            return new LinkedHashSet<>();
        }, key);
    }

    @Override
    public Set<String> zrevrangeByLex(String key, String max, String min) {
        //这里min、max与Jedis不一样，位置替换
        return (Set<String>) this.execute(() -> {
            List list = this.sync().zrangebylex(key, min, max);
            if (CollectionUtils.isNotEmpty(list)) {
                return new LinkedHashSet<>(list);
            }
            return new LinkedHashSet<>();
        }, key);
    }

    @Override
    public Set<String> zrevrangeByLex(String key, String max, String min, int offset, int count) {
        return (Set<String>) this.execute(() -> {
            List list = this.sync().zrangebylex(key, min, max, offset, count);
            if (CollectionUtils.isNotEmpty(list)) {
                return new LinkedHashSet<>(list);
            }
            return new LinkedHashSet<>();
        }, key);
    }

    @Override
    public Long zremrangeByLex(String key, String min, String max) {
        return this.execute(() -> this.sync().zremrangebylex(key, min, max), key);
    }

    @Override
    public Long strlen(String key) {
        return this.execute(() -> this.sync().strlen(key), key);
    }

    @Override
    public Long lpushx(String key, String... string) {
        return this.execute(() -> this.sync().lpushx(key, string), key);
    }

    @Override
    public Long rpushx(String key, String... string) {
        return this.execute(() -> this.sync().rpushx(key, string), key);
    }

    @Override
    public String echo(String string) {
        return (String) this.execute(() -> this.sync().echo(string), string);
    }

    @Override
    public Long linsert(String key, Client.LIST_POSITION where, String pivot, String value, int seconds) {
        return this.execute(() -> this.sync().linsert(key, where == Client.LIST_POSITION.BEFORE ? true : false, pivot, value), seconds, key);
    }

    @Override
    public String brpoplpush(String source, String destination, int timeout) {
        return (String) this.execute(() -> this.sync().brpoplpush(timeout, source, destination), source);
    }

    @Override
    public Boolean setbit(String key, long offset, boolean value, int seconds) {
        return this.execute(() -> this.sync().setbit(key, offset, value ? 1 : 0) > 0 ? Boolean.TRUE : Boolean.FALSE, seconds, key);
    }

    @Override
    public Boolean setbit(String key, long offset, String value, int seconds) {
        if (!RedisMagicConstants.ONE.equals(value) && !RedisMagicConstants.ZERO.equals(value)) {
            CacheExceptionFactory.throwException("Lettuce->setbit(String key, long offset, String value) value必须为0或1");
            return false;
        }
        return this.execute(() -> this.sync().setbit(key, offset, Integer.valueOf(value)) > 0 ? Boolean.TRUE : Boolean.FALSE, seconds, key);
    }

    @Override
    public Boolean getbit(String key, long offset) {
        return this.execute(() -> this.sync().getbit(key, offset) > 0 ? Boolean.TRUE : Boolean.FALSE, key);
    }

    @Override
    public Long setrange(String key, long offset, String value) {
        return this.execute(() -> this.sync().setrange(key, offset, value), key);
    }

    @Override
    public String getrange(String key, long startOffset, long endOffset) {
        return (String) this.execute(() -> this.sync().getrange(key, startOffset, endOffset), key);
    }

    @Override
    public Long bitpos(String key, boolean value) {
        return this.execute(() -> this.sync().bitpos(key, value), key);
    }

    @Override
    public Long bitpos(String key, boolean state, long start, long end) {
        return this.execute(() -> this.sync().bitpos(key, state, start, end), key);
    }

    @Override
    public void subscribe(JedisPubSub jedisPubSub, String[] channels) {
        StatefulRedisPubSubConnection<String, String> lettucePubSubConnection = this.getPubSubConnection();
        lettucePubSubConnection.addListener(new RedisPubSubListener() {
            @Override
            public void message(Object channel, Object message) {
                jedisPubSub.onMessage((String) channel, (String) message);
            }

            @Override
            public void message(Object pattern, Object channel, Object message) {
                jedisPubSub.onPMessage((String) pattern, (String) channel, (String) message);
            }

            @Override
            public void subscribed(Object channel, long count) {
                jedisPubSub.onSubscribe((String) channel, (int) count);
            }

            @Override
            public void psubscribed(Object pattern, long count) {
                jedisPubSub.onPSubscribe((String) pattern, (int) count);
            }

            @Override
            public void unsubscribed(Object channel, long count) {
                jedisPubSub.onUnsubscribe((String) channel, (int) count);
            }

            @Override
            public void punsubscribed(Object pattern, long count) {
                jedisPubSub.onPUnsubscribe((String) pattern, (int) count);
            }
        });
        this.execute(() -> {
            lettucePubSubConnection.sync().subscribe(channels);
            return null;
        }, channels);
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
        StatefulRedisPubSubConnection<String, String> lettucePubSubConnection = this.getPubSubConnection();
        lettucePubSubConnection.addListener(new RedisPubSubListener() {
            @Override
            public void message(Object channel, Object message) {
                pubSubModel.onMessage((String) message);
            }

            @Override
            public void message(Object pattern, Object channel, Object message) {
            }

            @Override
            public void subscribed(Object channel, long count) {
            }

            @Override
            public void psubscribed(Object pattern, long count) {
            }

            @Override
            public void unsubscribed(Object channel, long count) {
            }

            @Override
            public void punsubscribed(Object pattern, long count) {
            }
        });
        this.execute(() -> {
            lettucePubSubConnection.sync().subscribe(channels);
            return null;
        }, channels);
    }

    @Override
    public Long publish(String channel, String message) {
        return this.execute(() -> this.sync().publish(channel, message), channel);
    }

    @Override
    public void publishAsync(String channel, String message) {
        this.async().publish(channel, message);
    }

    @Override
    public void psubscribe(JedisPubSub jedisPubSub, String... patterns) {
        StatefulRedisPubSubConnection<String, String> lettucePubSubConnection = this.getPubSubConnection();
        lettucePubSubConnection.addListener(new RedisPubSubListener() {
            @Override
            public void message(Object channel, Object message) {
                jedisPubSub.onMessage((String) channel, (String) message);
            }

            @Override
            public void message(Object pattern, Object channel, Object message) {
                jedisPubSub.onPMessage((String) pattern, (String) channel, (String) message);
            }

            @Override
            public void subscribed(Object channel, long count) {
                jedisPubSub.onSubscribe((String) channel, (int) count);
            }

            @Override
            public void psubscribed(Object pattern, long count) {
                jedisPubSub.onPSubscribe((String) pattern, (int) count);
            }

            @Override
            public void unsubscribed(Object channel, long count) {
                jedisPubSub.onUnsubscribe((String) channel, (int) count);
            }

            @Override
            public void punsubscribed(Object pattern, long count) {
                jedisPubSub.onPUnsubscribe((String) pattern, (int) count);
            }
        });
        this.execute(() -> {
            lettucePubSubConnection.sync().psubscribe(patterns);
            return null;
        }, "");
    }

    @Override
    public List<Slowlog> slowlogGet() {
        //TODO 类型转换可能有问题
        return (List<Slowlog>) this.execute(() -> this.sync().slowlogGet());
    }

    @Override
    public List<Slowlog> slowlogGet(long entries) {
        return (List<Slowlog>) this.execute(() -> this.sync().slowlogGet((int) entries));
    }

    @Override
    public Long bitcount(String key) {
        return this.execute(() -> this.sync().bitcount(key), key);
    }

    @Override
    public Long bitcount(String key, long start, long end) {
        return this.execute(() -> this.sync().bitcount(key, start, end), key, new Object[]{start, end});
    }

    @Override
    public Long bitop(BitOP op, String destKey, String... srcKeys) {
        return this.execute(() -> {
            switch (op) {
                case OR:
                    return this.sync().bitopOr(destKey, srcKeys);
                case AND:
                    return this.sync().bitopAnd(destKey, srcKeys);
                case NOT:
                    return this.sync().bitopNot(destKey, srcKeys);
                default:
                    return this.sync().bitopXor(destKey, srcKeys);
            }
        }, srcKeys);
    }

    @Override
    public Boolean pexpire(String key, long milliseconds) {
        return this.execute(() -> this.sync().pexpire(key, milliseconds), key);
    }

    @Override
    public Boolean pexpireAt(String key, long millisecondsTimestamp) {
        return this.execute(() -> this.sync().pexpireat(key, millisecondsTimestamp), key);
    }

    @Override
    public Long pttl(String key) {
        return this.execute(() -> this.sync().pttl(key), key);
    }

    @Override
    public String psetex(String key, long milliseconds, String value) {
        return this.execute(() -> this.sync().psetex(key, milliseconds, value), key);
    }

    @Override
    public ScanResult<String> scan(String cursor) {
        return this.execute(() -> {
            KeyScanCursor scan = this.sync().scan(this.getScanCursor(cursor));
            if (null != scan) {
                return new ScanResult<String>(scan.getCursor(), scan.getKeys());
            }
            return null;
        });
    }

    @Override
    public ScanResult<String> scan(String cursor, ScanParams params) {
        return this.execute(() -> {
            KeyScanCursor scan = this.sync().scan(this.getScanCursor(cursor), this.scanParamsToScanArgs(params));
            if (null != scan) {
                return new ScanResult<String>(scan.getCursor(), scan.getKeys());
            }
            return null;
        });
    }

    @Override
    public ScanResult<Map.Entry<String, String>> hscan(String key, String cursor) {
        return this.execute(() -> {
            MapScanCursor scan = this.sync().hscan(key, this.getScanCursor(cursor));
            if (null != scan) {
                return new ScanResult<Map.Entry<String, String>>(scan.getCursor(), new ArrayList<>(scan.getMap().entrySet()));
            }
            return null;
        }, key);
    }

    @Override
    public ScanResult<Map.Entry<String, String>> hscan(String key, String cursor, ScanParams params) {
        return this.execute(() -> {
            MapScanCursor scan = this.sync().hscan(key, this.getScanCursor(cursor), this.scanParamsToScanArgs(params));
            if (null != scan) {
                return new ScanResult<Map.Entry<String, String>>(scan.getCursor(), new ArrayList<>(scan.getMap().entrySet()));
            }
            return null;
        }, key);
    }

    @Override
    public ScanResult<String> sscan(String key, String cursor) {
        return this.execute(() -> {
            ValueScanCursor scan = this.sync().sscan(key, this.getScanCursor(cursor));
            if (null != scan) {
                return new ScanResult<String>(scan.getCursor(), scan.getValues());
            }
            return null;
        }, key);
    }

    @Override
    public ScanResult<String> sscan(String key, String cursor, ScanParams params) {
        return this.execute(() -> {
            ValueScanCursor scan = this.sync().sscan(key, this.getScanCursor(cursor), this.scanParamsToScanArgs(params));
            if (null != scan) {
                return new ScanResult<String>(scan.getCursor(), scan.getValues());
            }
            return null;
        }, key);
    }

    @Override
    public ScanResult<Tuple> zscan(String key, String cursor) {
        return this.execute(() -> {
            ScoredValueScanCursor scan = this.sync().zscan(key, this.getScanCursor(cursor));
            if (null != scan) {
                return new ScanResult<Tuple>(scan.getCursor(), this.scoreValuesToTupleList(scan.getValues()));
            }
            return null;
        }, key);
    }

    @Override
    public ScanResult<Tuple> zscan(String key, String cursor, ScanParams params) {
        return this.execute(() -> {
            ScoredValueScanCursor scan = this.sync().zscan(key, this.getScanCursor(cursor), this.scanParamsToScanArgs(params));
            if (null != scan) {
                return new ScanResult<Tuple>(scan.getCursor(), this.scoreValuesToTupleList(scan.getValues()));
            }
            return null;
        }, key);
    }

    private ScanCursor getScanCursor(String cursor) {
        ScanCursor scanCursor = new ScanCursor();
        scanCursor.setCursor(cursor);
        return scanCursor;
    }

    private ScanArgs scanParamsToScanArgs(ScanParams params) {
        ScanArgs scanArgs = new ScanArgs();
        Collection<byte[]> paramsList = params.getParams();
        if (CollectionUtils.isEmpty(paramsList)) {
            CacheExceptionFactory.throwException("ScanParams to ScanArgs error ! ScanParams is empty !");
            return null;
        }
        Map<byte[], byte[]> paramsMap = new HashMap<>(paramsList.size() >> 1);
        byte[] key = null;
        int i = 0;
        for (byte[] bytes : paramsList) {
            i++;
            if (i % 2 > 0) {
                key = bytes;
            } else {
                paramsMap.put(key, bytes);
            }
        }
        if (null != paramsMap.get(COUNT.raw)) {
            scanArgs.limit(Long.parseLong(SafeEncoder.encode(paramsMap.get(COUNT.raw))));
        }
        if (null != paramsMap.get(MATCH.raw)) {
            scanArgs.match(new String(paramsMap.get(MATCH.raw)));
        }
        return scanArgs;
    }

    @Override
    public List<String> pubsubChannels(String pattern) {
        return (List<String>) this.execute(() -> this.sync().pubsubChannels(pattern));
    }

    @Override
    public Long pubsubNumPat() {
        return this.execute(() -> this.sync().pubsubNumpat());
    }

    @Override
    public Map<String, String> pubsubNumSub(String... channels) {
        return (Map<String, String>) this.execute(() -> this.sync().pubsubNumsub(channels));
    }

    @Override
    public Long pfadd(String key, int seconds, String... elements) {
        return this.execute(() -> this.sync().pfadd(key, elements), seconds, key);
    }

    @Override
    public long pfcount(String key) {
        return this.execute(() -> this.sync().pfcount(key), key);
    }

    @Override
    public long pfcount(String... keys) {
        return this.execute(() ->this.sync().pfcount(keys), keys);
    }

    @Override
    public String pfmerge(String destkey, int seconds, String... sourcekeys) {
        return this.execute(() -> this.sync().pfmerge(destkey, sourcekeys), seconds, sourcekeys);
    }

    @Override
    public List<String> blpop(int timeout, String key) {
        KeyValue execute = this.execute(() -> this.sync().blpop(timeout, key), key);
        if (null != execute) {
            ArrayList<String> strings = new ArrayList<>();
            strings.add(String.valueOf(execute.getValue()));
            return strings;
        }
        return null;
    }

    @Override
    public List<String> brpop(int timeout, String key) {
        KeyValue execute = this.execute(() -> this.sync().brpop(timeout, key), key);
        if (null != execute) {
            ArrayList<String> strings = new ArrayList<>();
            strings.add(String.valueOf(execute.getValue()));
            return strings;
        }
        return null;
    }

    @Override
    public Long geoadd(String key, double longitude, double latitude, String member, int seconds) {
        return this.execute(() -> this.sync().geoadd(key, longitude, latitude, member), seconds, key);
    }

    @Override
    public Long geoadd(String key, Map<String, GeoCoordinate> memberCoordinateMap, int seconds) {
        if (null != memberCoordinateMap && memberCoordinateMap.size() > 0) {
            return this.execute(() -> {
                Object[] params = new Object[memberCoordinateMap.size() * 3];
                int i = 0;
                for (Map.Entry<String, GeoCoordinate> stringGeoCoordinateEntry : memberCoordinateMap.entrySet()) {
                    params[i++] = stringGeoCoordinateEntry.getValue().getLongitude();
                    params[i++] = stringGeoCoordinateEntry.getValue().getLatitude();
                    params[i++] = stringGeoCoordinateEntry.getKey();
                }
                return this.sync().geoadd(key, params);
            }, seconds, key);
        }
        return 0L;
    }

    @Override
    public Double geodist(String key, String member1, String member2) {
        return this.execute(() -> this.sync().geodist(key, member1, member2, GeoArgs.Unit.m), key);
    }

    @Override
    public Double geodist(String key, String member1, String member2, GeoUnit unit) {
        return this.execute(() -> this.sync().geodist(key, member1, member2, this.geoUnitToGeoArgs(unit)), key);
    }

    @Override
    public List<String> geohash(String key, String... members) {
        return this.execute(() -> {
            List<Value<String>> geohash = this.sync().geohash(key, members);
            if (CollectionUtils.isNotEmpty(geohash)) {
                List<String> list = new ArrayList<>();
                for (Value<String> stringValue : geohash) {
                    stringValue.ifHasValue(t -> {
                        list.add(stringValue.getValue());
                    });
                    stringValue.ifEmpty(() -> {
                        list.add(null);
                    });
                }
                return list;
            }
            return null;
        }, key);
    }

    private GeoCoordinate geoCoordinatesToGeoCoordinate(GeoCoordinates geoCoordinates) {
        if (null != geoCoordinates) {
            return new GeoCoordinate(geoCoordinates.getX().doubleValue(), geoCoordinates.getY().doubleValue());
        }
        return null;
    }

    /**
     * 经纬度转换
     *
     * @param list
     * @return
     */
    private List<GeoCoordinate> geoCoordinatesToGeoCoordinate(List<GeoCoordinates> list) {
        if (CollectionUtils.isNotEmpty(list)) {
            List<GeoCoordinate> res = new ArrayList<>();
            for (GeoCoordinates geoCoordinates : list) {
                res.add(this.geoCoordinatesToGeoCoordinate(geoCoordinates));
            }
            return res;
        }
        return null;
    }

    @Override
    public List<GeoCoordinate> geopos(String key, String... members) {
        return (List<GeoCoordinate>) this.execute(() -> this.geoCoordinatesToGeoCoordinate(this.sync().geopos(key, members)), key);
    }

    private List<GeoRadiusResponse> geoWithinsToGeoRadiusResponses(List<GeoWithin> list) {
        if (CollectionUtils.isNotEmpty(list)) {
            List<GeoRadiusResponse> res = new ArrayList<>();
            for (GeoWithin geoWithin : list) {
                GeoRadiusResponse geoRadiusResponse = new GeoRadiusResponse(geoWithin.getMember().toString().getBytes(Charset.defaultCharset()));
                if (null != geoWithin.getDistance()) {
                    geoRadiusResponse.setDistance(geoWithin.getDistance());
                }
                if (null != geoWithin.getCoordinates()) {
                    geoRadiusResponse.setCoordinate(this.geoCoordinatesToGeoCoordinate(geoWithin.getCoordinates()));
                }
                res.add(geoRadiusResponse);
            }
            return res;
        }
        return null;
    }

    @Override
    public List<GeoRadiusResponse> georadius(String key, double longitude, double latitude, double radius, GeoUnit unit, GeoRadiusParam param) {
        return (List<GeoRadiusResponse>) this.execute(() -> this.geoWithinsToGeoRadiusResponses(this.sync().georadius(key, longitude, latitude, radius, this.geoUnitToGeoArgs(unit), this.geoRadiusParamToGeoArgs(param))), key);
    }

//    @Override
//    public List<GeoRadiusResponse> georadiusByMember(String key, String member, double radius, GeoUnit unit) {
//        return (List<GeoRadiusResponse>) this.execute(() -> {
//            //TODO
//            return this.sync().georadiusbymember(key, member, radius, this.geoUnitToGeoArgs(unit));
//        },Thread.currentThread() .getStackTrace()[1].getMethodName(),key);
//    }

    @Override
    public List<GeoRadiusResponse> georadiusByMember(String key, String member, double radius, GeoUnit unit, GeoRadiusParam param) {
        return (List<GeoRadiusResponse>) this.execute(() -> this.geoWithinsToGeoRadiusResponses(this.sync().georadiusbymember(key, member, radius, this.geoUnitToGeoArgs(unit), this.geoRadiusParamToGeoArgs(param))), key);
    }

    private GeoArgs.Unit geoUnitToGeoArgs(GeoUnit unit) {
        switch (unit) {
            case M:
                return GeoArgs.Unit.m;
            case KM:
                return GeoArgs.Unit.km;
            case MI:
                return GeoArgs.Unit.mi;
            case FT:
                return GeoArgs.Unit.ft;
            default:
                return GeoArgs.Unit.m;
        }
    }

    private GeoArgs geoRadiusParamToGeoArgs(GeoRadiusParam param) {
        GeoArgs geoArgs = new GeoArgs();
        if (null != param) {
            if (param.contains(RedisMagicConstants.WITHCOORD)) {
                geoArgs.withCoordinates();
            }
            if (param.contains(RedisMagicConstants.WITHDIST)) {
                geoArgs.withDistance();
            }
            if (param.contains(RedisMagicConstants.ASC)) {
                geoArgs.asc();
            }
            if (param.contains(RedisMagicConstants.DESC)) {
                geoArgs.desc();
            }
            if (param.contains(RedisMagicConstants.COUNT)) {
                geoArgs.withCount(Long.parseLong(param.getParam("count").toString()));
            }
        }
        return geoArgs;
    }

    @Override
    public List<Long> bitfield(String key, String... arguments) {
        CacheExceptionFactory.throwException("请使用 Lettuce->bitfield(String key, BitFieldArgs bitFieldArgs)");
        return null;
    }

    @Override
    public List<Long> bitfield(String key, BitFieldArgs bitFieldArgs) {
        return (List<Long>) this.execute(() -> this.sync().bitfield(key, bitFieldArgs), key);
    }

    @Override
    public Long hstrlen(String key, String field) {
        return (Long) this.execute(() -> {
            return this.sync().hstrlen(key, field);
        }, key);
    }

    @Override
    public Object eval(String script, ScriptOutputType outputType, int keyCount, String[] params) {
        String[] keys = new String[params.length > 1 ? params.length >> 1 : params.length];
        String[] values = new String[params.length >> 1];
        for (int i = 0; i < params.length; i++) {
            //如果是奇数
            if ((i & 1) == 1) {
                values[(i - 1) >> 1] = params[i];
            } else {
                keys[i >> 1] = params[i];
            }
        }
        return this.execute(() -> {
            return this.sync().eval(script, outputType, keys, values);
        });
    }

    @Override
    public Object eval(String script, ScriptOutputType outputType, List<String> keys, List<String> args) {
        return this.execute(() -> {
            return this.sync().eval(script, outputType, keys.toArray(new String[keys.size()]), args.toArray());
        });
    }

    @Override
    public Object eval(String script, ScriptOutputType outputType) {
        return this.execute(() -> {
            return this.sync().eval(script, outputType);
        });
    }

    @Override
    public Object evalsha(String sha1, ScriptOutputType outputType) {
        return this.execute(() -> {
            return this.sync().evalsha(sha1, outputType);
        });
    }

    @Override
    public Object evalsha(String sha1, ScriptOutputType outputType, List<String> keys, List<String> args) {
        return this.execute(() -> {
            return this.sync().evalsha(sha1, outputType, keys.toArray(new String[keys.size()]), args.toArray());
        });
    }

    @Override
    public Object evalsha(String sha1, ScriptOutputType outputType, int keyCount, String[] params) {
        String[] keys = new String[params.length > 1 ? params.length >> 1 : params.length];
        String[] values = new String[params.length >> 1];
        for (int i = 0; i < params.length; i++) {
            //如果是奇数
            if ((i & 1) == 1) {
                values[(i - 1) >> 1] = params[i];
            } else {
                keys[i >> 1] = params[i];
            }
        }
        return this.execute(() -> {
            return this.sync().evalsha(sha1, outputType, keys, values);
        });
    }

    @Override
    public Boolean scriptExists(String sha1) {
        return (Boolean) this.execute(() -> {
            List list = this.sync().scriptExists(sha1);
            if (CollectionUtils.isNotEmpty(list)) {
                return list.get(0);
            }
            return false;
        });
    }

    @Override
    public List<Boolean> scriptExists(String... sha1) {
        return (List<Boolean>) this.execute(() -> {
            return this.sync().scriptExists(sha1);
        });
    }

    @Override
    public String scriptLoad(String script) {
        return (String) this.execute(() -> {
            return this.sync().scriptLoad(script);
        });
    }
}
