package com.lcache.test.assertTest;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.ImmutableMap;
import com.lcache.client.CacheClientFactory;
import com.lcache.core.BaseCacheExecutor;
import com.lcache.core.cache.redis.lua.RedisLuaInterface;
import com.lcache.core.constant.ConnectTypeEnum;
import com.lcache.core.constant.RedisClientConstants;
import com.lcache.core.model.CacheConfigModel;
import com.lcache.core.model.CacheDataBuilder;
import com.lcache.extend.handle.redis.jedis.config.JedisConnectSourceConfig;
import com.lcache.extend.handle.redis.lettuce.config.LettuceConnectSourceConfig;
import io.lettuce.core.BitFieldArgs;
import io.lettuce.core.ScriptOutputType;
import io.lettuce.core.SortArgs;
import org.junit.Test;
import redis.clients.jedis.*;
import redis.clients.jedis.params.geo.GeoRadiusParam;
import redis.clients.jedis.params.sortedset.ZAddParams;
import redis.clients.jedis.params.sortedset.ZIncrByParams;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class assertTest {

    private BaseCacheExecutor jedis = CacheClientFactory.getCacheExecutor(new CacheConfigModel().setClientType(RedisClientConstants.JEDIS).setConnectTypeEnum(ConnectTypeEnum.POOL).setCacheType("test"), new JedisConnectSourceConfig());
    private BaseCacheExecutor lettuce = CacheClientFactory.getCacheExecutor(new CacheConfigModel().setClientType(RedisClientConstants.LETTUCE).setConnectTypeEnum(ConnectTypeEnum.SIMPLE).setCacheType("test1"), new LettuceConnectSourceConfig());

    private static final int seconds = 3600;

    @Test
    public void getExecutor() {
        assertEquals(jedis, CacheClientFactory.getCacheExecutor(new CacheConfigModel().setClientType(RedisClientConstants.JEDIS).setConnectTypeEnum(ConnectTypeEnum.POOL).setCacheType("test"), new JedisConnectSourceConfig()));
        assertEquals(lettuce, CacheClientFactory.getCacheExecutor(new CacheConfigModel().setClientType(RedisClientConstants.LETTUCE).setConnectTypeEnum(ConnectTypeEnum.SIMPLE).setCacheType("test1"), new LettuceConnectSourceConfig()));
        assertEquals(jedis, CacheClientFactory.builder().cacheType("test").clientType(RedisClientConstants.JEDIS).connectType(ConnectTypeEnum.POOL).cacheConfig(new JedisConnectSourceConfig()).build());
        assertEquals(lettuce, CacheClientFactory.builder().cacheType("test1").clientType(RedisClientConstants.LETTUCE).connectType(ConnectTypeEnum.SIMPLE).cacheConfig(new LettuceConnectSourceConfig()).build());
    }

    @Test
    public void set1() {
        assertEquals(jedis.set("jSet1", "1", seconds), lettuce.set("lSet1", "1", seconds));
        assertEquals(jedis.set("jSet1", "1", seconds), lettuce.set("lSet1", "1", seconds));
        lettuce.del("jSet1");
        lettuce.del("lSet1");
        System.out.println("set1 通过 !");
    }

    @Test
    public void set2() {
        assertEquals(jedis.set("jset2", "2", "nx", "ex", 60), lettuce.set("lset2", "2", "nx", "ex", 60));
        assertEquals(jedis.set("jset2", "2", "nx", "ex", 60), lettuce.set("lset2", "2", "nx", "ex", 60));
        lettuce.del("jset2");
        lettuce.del("lset2");
        System.out.println("set2 通过 !");
    }

    @Test
    public void get() {
        lettuce.set("getTest", "1", seconds);
        assertEquals(jedis.get("getTest"), lettuce.get("getTest"));
        assertEquals(jedis.get("null"), lettuce.get("null"));
        lettuce.del("getTest");
        System.out.println("getTest 通过 !");
    }

    @Test
    public void exists() {
        lettuce.set("exists", "1", seconds);
        assertEquals(jedis.exists("exists"), lettuce.exists("exists"));
        assertEquals(jedis.exists("null"), lettuce.exists("null"));
        lettuce.del("exists");
        System.out.println("exists 通过 !");
    }

    @Test
    public void del() {
        lettuce.set("jdel", "1", seconds);
        lettuce.set("ldel", "1", seconds);
        assertEquals(jedis.del("jdel"), lettuce.del("ldel"));
        assertEquals(jedis.del("null"), lettuce.del("null"));
        System.out.println("del 通过 !");
    }

    @Test
    public void expire() {
        lettuce.set("expire", "1", seconds);
        assertEquals(jedis.expire("expire", 60), lettuce.expire("expire", 60));
        assertEquals(jedis.expire("null", 60), lettuce.expire("null", 60));
        lettuce.del("expire");
        System.out.println("expire 通过 !");
    }

    @Test
    public void expireAt() {
        lettuce.set("expireAt", "1", seconds);
        //时间设置错误时，Lettuce会返回0，Jedis返回1
        assertEquals(jedis.expireAt("expireAt", System.currentTimeMillis() + 5000), lettuce.expireAt("expireAt", System.currentTimeMillis() + 5000));
        assertEquals(jedis.expireAt("null", System.currentTimeMillis() + 5000), lettuce.expireAt("null", System.currentTimeMillis() + 5000));
        lettuce.del("expireAt");
        System.out.println("expireAt 通过 !");
    }

    @Test
    public void ttl() {
        lettuce.set("ttl", "1", seconds);
        assertEquals(jedis.ttl("ttl"), lettuce.ttl("ttl"));
        lettuce.del("ttl");
        System.out.println("ttl 通过 !");
    }

    @Test
    public void getSet() {
        lettuce.set("jgetSet", "1", seconds);
        lettuce.set("lgetSet", "1", seconds);
        assertEquals(jedis.getSet("jgetSet", "2"), lettuce.getSet("lgetSet", "2"));
        lettuce.del("jgetSet");
        lettuce.del("lgetSet");
        System.out.println("getSet 通过 !");
    }

    @Test
    public void mget() {
        lettuce.set("mget1", "1", seconds);
        lettuce.set("mget2", "2", seconds);
        assertEquals(jedis.mget("set1", "set2"), lettuce.mget("set1", "set2"));
        lettuce.del("mget1");
        lettuce.del("mget2");
        System.out.println("mget 通过 !");
    }

    @Test
    public void setnx() {
        assertEquals(jedis.setnx("jsetnx", "1", seconds), lettuce.setnx("lsetnx", "1", seconds));
        lettuce.del("jsetnx");
        lettuce.del("lsetnx");
        System.out.println("setnx 通过 !");
    }

    @Test
    public void setex() {
        assertEquals(jedis.setnx("jsetex", "1", seconds), lettuce.setnx("lsetex", "1", seconds));
        lettuce.del("jsetex");
        lettuce.del("lsetex");
        System.out.println("setex 通过 !");
    }

    @Test
    public void mset() {
        assertEquals(jedis.mset(seconds, "jmset1", "1", "jmset2", "2"), lettuce.mset(seconds, "lmset1", "1", "lmset2", "2"));
        assertEquals(jedis.msetnx(seconds, "jmset1", "1", "jmset2", "2"), lettuce.msetnx(seconds, "lmset1", "1", "lmset2", "2"));
        lettuce.del("jmset1");
        lettuce.del("jmset2");
        lettuce.del("lmset1");
        lettuce.del("lmset2");
        System.out.println("mset 通过 !");
    }

    @Test
    public void decrAndIncr() {
        assertEquals(jedis.decrBy("jdecrby", 1, seconds), lettuce.decrBy("ldecrby", 1, seconds));
        lettuce.del("jdecrby");
        lettuce.del("ldecrby");
        assertEquals(jedis.decr("jdecr", seconds), lettuce.decr("ldecr", seconds));
        lettuce.del("jdecr");
        lettuce.del("ldecr");
        assertEquals(jedis.incrBy("jincrBy", 1, seconds), lettuce.incrBy("lincrBy", 1, seconds));
        lettuce.del("jincrBy");
        lettuce.del("lincrBy");
        assertEquals(jedis.incrByFloat("jincrByFloat", 1, seconds), lettuce.incrByFloat("lincrByFloat", 1, seconds));
        lettuce.del("jincrByFloat");
        lettuce.del("lincrByFloat");
        assertEquals(jedis.decr("jincr", seconds), lettuce.decr("lincr", seconds));
        lettuce.del("jincr");
        lettuce.del("lincr");
        System.out.println("decrAndIncr 通过 !");
    }

    @Test
    public void append() {
        assertEquals(jedis.append("jappend", "a"), lettuce.append("lappend", "a"));
        assertEquals(jedis.append("jappend", "a"), lettuce.append("lappend", "a"));
        lettuce.del("jappend");
        lettuce.del("lappend");
        System.out.println("append 通过 !");
    }

    @Test
    public void substr() {
        lettuce.set("jappend", "aaa", seconds);
        lettuce.set("lappend", "aaa", seconds);
        assertEquals(jedis.substr("jsubstr", 0, 2), lettuce.substr("jsubstr", 0, 2));
        assertEquals(jedis.strlen("jsubstr"), lettuce.strlen("jsubstr"));
        lettuce.del("jappend");
        lettuce.del("lappend");
        System.out.println("substr 通过 !");
    }

    @Test
    public void hset() {
        assertEquals(jedis.hset("jhset", "a", "a", seconds), lettuce.hset("lhset", "a", "a", seconds));
        lettuce.del("jhset");
        lettuce.del("lhset");
        System.out.println("hset 通过 !");
    }

    @Test
    public void hsetnx() {
        assertEquals(jedis.hsetnx("jhsetnx", "a", "a", seconds), lettuce.hsetnx("lhsetnx", "a", "a", seconds));
        assertEquals(jedis.hsetnx("jhsetnx", "a", "a", seconds), lettuce.hsetnx("lhsetnx", "a", "a", seconds));
        lettuce.del("jhsetnx");
        lettuce.del("lhsetnx");
        System.out.println("hsetnx 通过 !");
    }

    @Test
    public void hmset() {
        Map<String, String> hash = new HashMap<>();
        hash.put("a", "a");
        hash.put("b", "b");
        assertEquals(jedis.hmset("jhmset", hash, seconds), lettuce.hmset("lhmset", hash, seconds));
        assertEquals(jedis.hmget("jhmset", new String[]{"a", "b"}), lettuce.hmget("lhmset", new String[]{"a", "b"}));
        assertEquals(jedis.hmgetToMap("jhmset", new String[]{"a", "b", "c"}), lettuce.hmgetToMap("lhmset", new String[]{"a", "b", "c"}));
        assertEquals(jedis.hkeys("jhmset"), lettuce.hkeys("lhmset"));
        assertEquals(jedis.hvals("jhmset").stream().collect(Collectors.toSet()), lettuce.hvals("lhmset").stream().collect(Collectors.toSet()));
        assertEquals(jedis.hgetAll("jhmset"), lettuce.hgetAll("lhmset"));
        assertEquals(jedis.hexists("jhmset", "a"), lettuce.hexists("lhmset", "a"));
        assertEquals(jedis.hexists("jhmset", "c"), lettuce.hexists("lhmset", "c"));
        ScanParams scanParams = new ScanParams();
        scanParams.count(2);
        scanParams.match("a");
        assertEquals(jedis.hscan("jhmset", "0").getResult().stream().collect(Collectors.toSet()), lettuce.hscan("lhmset", "0").getResult().stream().collect(Collectors.toSet()));
        assertEquals(jedis.hscan("jhmset", "0", scanParams).getResult(), lettuce.hscan("lhmset", "0", scanParams).getResult());
        assertEquals(jedis.hscan("null", "0").getResult(), lettuce.hscan("null", "0").getResult());
        assertEquals(jedis.hscan("null", "0", scanParams).getResult(), lettuce.hscan("null", "0", scanParams).getResult());
        lettuce.del("jhmset");
        lettuce.del("lhmset");
        System.out.println("hmset 通过 !");
    }

    @Test
    public void hincrBy() {
        jedis.hset("jhincrBy", "a", "1", seconds);
        lettuce.hset("lhincrBy", "a", "1", seconds);
        assertEquals(jedis.hincrBy("jhincrBy", "a", 1, seconds), lettuce.hincrBy("lhincrBy", "a", 1, seconds));
        assertEquals(jedis.hincrByFloat("jhincrBy", "a", 1, seconds), lettuce.hincrByFloat("lhincrBy", "a", 1, seconds));
        lettuce.del("jhincrBy");
        lettuce.del("lhincrBy");
        System.out.println("hincrBy 通过 !");
    }

    @Test
    public void sadd() {
        assertEquals(jedis.sadd("jsadd", "a", seconds), lettuce.sadd("lsadd", "a", seconds));
        assertEquals(jedis.sadd("jsadd", new String[]{"a", "b"}, seconds), lettuce.sadd("lsadd", new String[]{"a", "b"}, seconds));
        assertEquals(jedis.sismember("jsadd", "a"), lettuce.sismember("lsadd", "a"));
        assertEquals(jedis.sismember("jsadd", "c"), lettuce.sismember("lsadd", "c"));
        assertEquals(jedis.smembers("jsadd"), lettuce.smembers("lsadd"));
        assertEquals(jedis.sinter("jsadd"), lettuce.sinter("lsadd"));
        assertEquals(jedis.sinterstore("jsadd1", seconds, "jsadd"), lettuce.sinterstore("lsadd1", seconds, "lsadd"));
        assertEquals(jedis.sunion("jsadd"), lettuce.sunion("lsadd"));
        assertEquals(jedis.sunionstore("jsadd2", seconds, "jsadd"), lettuce.sunionstore("lsadd2", seconds, "lsadd"));
        assertEquals(jedis.srem("jsadd1", "a"), lettuce.srem("lsadd1", "a"));
        assertEquals(jedis.spop("jsadd1"), lettuce.spop("lsadd1"));
        assertEquals(jedis.spop("jsadd2", 2), lettuce.spop("lsadd2", 2));
        assertEquals(jedis.smove("jsadd1", "jsadd2", "b"), lettuce.smove("lsadd1", "lsadd2", "b"));
        assertEquals(jedis.scard("jsadd2"), lettuce.scard("lsadd2"));
        assertEquals(jedis.srandmember("jsadd2"), lettuce.srandmember("jsadd2"));
        assertEquals(jedis.srandmember("jsadd2", 2), lettuce.srandmember("jsadd2", 2));
        assertEquals(jedis.sdiff("jsadd1", "jsadd2"), lettuce.sdiff("lsadd2"));
        assertEquals(jedis.sdiff("jsadd3", "jsadd1", "jsadd2"), lettuce.sdiff("lsadd3", "lsadd1", "lsadd2"));
        ScanParams scanParams = new ScanParams();
        scanParams.count(2);
        scanParams.match("a");
        assertEquals(jedis.sscan("jsadd", "0").getResult(), lettuce.sscan("lsadd", "0").getResult());
        assertEquals(jedis.sscan("jsadd", "0", scanParams).getResult(), lettuce.sscan("lsadd", "0", scanParams).getResult());
        assertEquals(jedis.sscan("jsadd3", "0").getResult(), lettuce.sscan("lsadd3", "0").getResult());
        assertEquals(jedis.sscan("jsadd3", "0", scanParams).getResult(), lettuce.sscan("lsadd3", "0", scanParams).getResult());
        assertEquals(jedis.sscan("null", "0").getResult(), lettuce.sscan("null", "0").getResult());
        assertEquals(jedis.sscan("null", "0", scanParams).getResult(), lettuce.sscan("null", "0", scanParams).getResult());
        lettuce.del("jsadd");
        lettuce.del("lsadd");
        lettuce.del("jsadd1");
        lettuce.del("lsadd1");
        lettuce.del("jsadd2");
        lettuce.del("lsadd2");
        lettuce.del("jsadd3");
        lettuce.del("lsadd3");
        System.out.println("set 通过 !");
    }

    @Test
    public void zadd() {
        assertEquals(jedis.zadd("jzadd", 1, "1", seconds), lettuce.zadd("lzadd", 1, "1", seconds));
        ZAddParams params = ZAddParams.zAddParams().nx().ch();
        assertEquals(jedis.zadd("jzadd", 1, "1", params, seconds), lettuce.zadd("lzadd", 1, "1", params, seconds));
        Map<String, Double> scoreMembers = new HashMap<>();
        scoreMembers.put("2", (double) 2);
        scoreMembers.put("3", (double) 3);
        assertEquals(jedis.zadd("jzadd", scoreMembers, seconds), lettuce.zadd("lzadd", scoreMembers, seconds));
        assertEquals(jedis.zadd("jzadd", scoreMembers, params, seconds), lettuce.zadd("lzadd", scoreMembers, params, seconds));
        assertEquals(jedis.zrem("jzadd", "3"), lettuce.zrem("lzadd", "3"));
        assertEquals(jedis.zincrby("jzadd", 1, "3", seconds), lettuce.zincrby("lzadd", 1, "3", seconds));
        ZIncrByParams params1 = ZIncrByParams.zIncrByParams().xx();
        assertEquals(jedis.zincrby("jzadd", 1, "3", params1, seconds), lettuce.zincrby("lzadd", 1, "3", params1, seconds));
        lettuce.del("jzadd");
        lettuce.del("lzadd");
        System.out.println("zadd 通过 !");
    }

    @Test
    public void zrank() {
        Map<String, Double> scoreMembers = new HashMap<>();
        scoreMembers.put("a", (double) -1);
        scoreMembers.put("b", (double) 0);
        scoreMembers.put("1", (double) 1);
        scoreMembers.put("2", (double) 2);
        scoreMembers.put("3", (double) 3);
        assertEquals(jedis.zadd("jzadd", scoreMembers, seconds), lettuce.zadd("lzadd", scoreMembers, seconds));
        assertEquals(jedis.zrank("jzadd", "2"), lettuce.zrank("lzadd", "2"));
        assertEquals(jedis.zrevrank("jzadd", "3"), lettuce.zrevrank("lzadd", "3"));
        assertEquals(jedis.zrange("jzadd", 0, 2), lettuce.zrange("lzadd", 0, 2));
        assertEquals(jedis.zrevrange("jzadd", 0, 2), lettuce.zrevrange("lzadd", 0, 2));
        assertEquals(jedis.zrangeWithScores("jzadd", 0, 2), lettuce.zrangeWithScores("lzadd", 0, 2));
        assertEquals(jedis.zrevrangeWithScores("jzadd", 0, 2), lettuce.zrevrangeWithScores("lzadd", 0, 2));
        assertEquals(jedis.zcard("jzadd"), lettuce.zcard("lzadd"));
        assertEquals(jedis.zscore("jzadd", "2"), lettuce.zscore("lzadd", "2"));
        assertEquals(jedis.zcount("jzadd", 0, 2), lettuce.zcount("lzadd", 0, 2));
        assertEquals(jedis.zcount("jzadd", "0", "2"), lettuce.zcount("lzadd", "0", "2"));
        assertEquals(jedis.zrangeByScore("jzadd", 0, 2), lettuce.zrangeByScore("lzadd", 0, 2));
        assertEquals(jedis.zrangeByScore("jzadd", "0", "2"), lettuce.zrangeByScore("lzadd", "0", "2"));
        assertEquals(jedis.zrangeByScore("jzadd", 0, 2, 0, 2), lettuce.zrangeByScore("lzadd", 0, 2, 0, 2));
        assertEquals(jedis.zrangeByScore("jzadd", "0", "2", 0, 2), lettuce.zrangeByScore("lzadd", "0", "2", 0, 2));
        assertEquals(jedis.zrangeByScoreWithScores("jzadd", 0, 2), lettuce.zrangeByScoreWithScores("lzadd", 0, 2));
        assertEquals(jedis.zrangeByScoreWithScores("jzadd", "0", "2"), lettuce.zrangeByScoreWithScores("lzadd", "0", "2"));
        assertEquals(jedis.zrangeByScoreWithScores("jzadd", 0, 2, 0, 2), lettuce.zrangeByScoreWithScores("lzadd", 0, 2, 0, 2));
        assertEquals(jedis.zrangeByScoreWithScores("jzadd", "0", "2", 0, 2), lettuce.zrangeByScoreWithScores("lzadd", "0", "2", 0, 2));
        assertEquals(jedis.zrevrangeByScore("jzadd", 2, 0), lettuce.zrevrangeByScore("lzadd", 2, 0));
        assertEquals(jedis.zrevrangeByScore("jzadd", "0", "2"), lettuce.zrevrangeByScore("lzadd", "2", "0"));
        assertEquals(jedis.zrevrangeByScoreWithScores("jzadd", 2, 0, 0, 2), lettuce.zrevrangeByScoreWithScores("lzadd", 2, 0, 0, 2));
        assertEquals(jedis.zrevrangeByScoreWithScores("jzadd", "2", "0", 0, 2), lettuce.zrevrangeByScoreWithScores("lzadd", "2", "0", 0, 2));
        assertEquals(jedis.zremrangeByScore("jzadd", 0, 2), lettuce.zremrangeByScore("lzadd", 0, 2));
        assertEquals(jedis.zremrangeByRank("jzadd", 0, 2), lettuce.zremrangeByRank("lzadd", 0, 2));
        ZParams zParams = new ZParams().weightsByDouble(1).aggregate(ZParams.Aggregate.SUM);
        assertEquals(jedis.zunionstore("jzadd1", "jzadd"), lettuce.zunionstore("lzadd1", "lzadd"));
        assertEquals(jedis.zunionstore("jzadd1", zParams, "jzadd"), lettuce.zunionstore("lzadd1", zParams, "lzadd"));
        assertEquals(jedis.zinterstore("jzadd1", "jzadd"), lettuce.zinterstore("lzadd1", "lzadd"));
        assertEquals(jedis.zinterstore("jzadd1", zParams, "jzadd"), lettuce.zinterstore("lzadd1", zParams, "lzadd"));
        assertEquals(jedis.zlexcount("jzadd", "[1", "[3"), lettuce.zlexcount("lzadd", "[1", "[3"));
        assertEquals(jedis.zrangeByLex("jzadd", "[1", "[3"), lettuce.zrangeByLex("lzadd", "[1", "[3"));
        assertEquals(jedis.zrangeByLex("jzadd", "[1", "[3", 0, 2), lettuce.zrangeByLex("lzadd", "[1", "[3", 0, 2));
        assertEquals(jedis.zrevrangeByLex("jzadd", "[1", "[3"), lettuce.zrevrangeByLex("lzadd", "[1", "[3"));
        assertEquals(jedis.zrevrangeByLex("jzadd", "[1", "[3", 0, 2), lettuce.zrevrangeByLex("lzadd", "[1", "[3", 0, 2));
        assertEquals(jedis.zremrangeByLex("jzadd", "[1", "[3"), lettuce.zremrangeByLex("lzadd", "[1", "[3"));
        SortingParams sortingParams = new SortingParams();
        sortingParams.desc();
        sortingParams.alpha();
        SortArgs sortArgs = SortArgs.Builder.alpha().desc();
        assertEquals(jedis.sort("jzadd"), lettuce.sort("lzadd"));
        assertEquals(jedis.sort("jzadd", sortingParams), lettuce.sort("lzadd", sortArgs));
        assertEquals(jedis.sort("jzadd", sortingParams, "jzadd2"), lettuce.sort("lzadd", sortArgs, "lzadd2"));
        assertEquals(jedis.sort("jzadd", "jzadd2"), lettuce.sort("lzadd", "lzadd2"));
        ScanParams scanParams = new ScanParams();
        scanParams.count(2);
        scanParams.match("a");
        assertEquals(jedis.zscan("jzadd", "0").getResult(), lettuce.zscan("lzadd", "0").getResult());
        assertEquals(jedis.zscan("jzadd", "0", scanParams).getResult(), lettuce.zscan("lzadd", "0", scanParams).getResult());
        assertEquals(jedis.zscan("null", "0").getResult(), lettuce.zscan("null", "0").getResult());
        assertEquals(jedis.zscan("null", "0", scanParams).getResult(), lettuce.zscan("null", "0", scanParams).getResult());
        lettuce.del("jzadd");
        lettuce.del("lzadd");
        lettuce.del("jzadd1");
        lettuce.del("lzadd1");
        lettuce.del("jzadd2");
        lettuce.del("lzadd2");
        System.out.println("zrank 通过 !");
    }

    @Test
    public void pushx() {
        assertEquals(jedis.lpush("jlpushx", "1", seconds), lettuce.lpush("llpushx", "1", seconds));
        assertEquals(jedis.lpushx("jlpushx", "a"), lettuce.lpushx("llpushx", "a"));
        assertEquals(jedis.rpushx("jlpushx", "b"), lettuce.rpushx("llpushx", "b"));
        assertEquals(jedis.llen("jlpushx"), lettuce.llen("llpushx"));
        assertEquals(jedis.lindex("jlpushx", 1), lettuce.lindex("llpushx", 1));
        assertEquals(jedis.lrange("jlpushx", 0, 5), lettuce.lrange("llpushx", 0, 5));
        assertEquals(jedis.lset("jlpushx", 0, "a1", seconds), lettuce.lset("llpushx", 0, "a1", seconds));
        assertEquals(jedis.ltrim("jlpushx", 0, 1), lettuce.ltrim("llpushx", 0, 1));
        assertEquals(jedis.ltrim("null", 0, 1), lettuce.ltrim("null", 0, 1));
        assertEquals(jedis.lrem("jlpushx", 0, "a1"), lettuce.lrem("llpushx", 0, "a1"));
        assertEquals(jedis.linsert("jlpushx", BinaryClient.LIST_POSITION.AFTER, "b", "c", seconds), lettuce.linsert("llpushx", BinaryClient.LIST_POSITION.AFTER, "b", "c", seconds));
        //lettuce阻塞时间不能超过命令最大执行时间
        assertEquals(jedis.brpoplpush("jlpushx", "jlpushx1", 1), lettuce.brpoplpush("llpushx", "llpushx1", 1));
        assertEquals(jedis.rpoplpush("jlpushx", "jlpushx1", seconds), lettuce.rpoplpush("jlpushx", "jlpushx1", seconds));
        assertEquals(jedis.rpop("jlpushx"), lettuce.rpop("llpushx"));
        assertEquals(jedis.lpop("jlpushx"), lettuce.lpop("llpushx"));
        assertEquals(jedis.echo("jlpushx"), lettuce.echo("jlpushx"));
        assertEquals(jedis.echo("null"), lettuce.echo("null"));
        lettuce.del("jlpushx");
        lettuce.del("llpushx");
        lettuce.del("jlpushx1");
        lettuce.del("llpushx1");
        System.out.println("pushx 通过 !");
    }

    @Test
    public void bitmap() {
        assertEquals(jedis.setbit("jbit", 0, true, seconds), lettuce.setbit("lbit", 0, true, seconds));
        assertEquals(jedis.setbit("jbit", 1, "1", seconds), lettuce.setbit("lbit", 1, "1", seconds));
        assertEquals(jedis.getbit("jbit", 1), lettuce.getbit("lbit", 1));
        assertEquals(jedis.getbit("null", 1), lettuce.getbit("null", 1));
        assertEquals(jedis.bitpos("jbit", true), lettuce.bitpos("lbit", true));
        assertEquals(jedis.bitpos("null", true), lettuce.bitpos("null", true));
        assertEquals(jedis.bitpos("jbit", true, 0, 5), lettuce.bitpos("lbit", true, 0, 5));
        assertEquals(jedis.bitpos("null", true, 0, 5), lettuce.bitpos("null", true, 0, 5));
        assertEquals(jedis.bitcount("jbit"), lettuce.bitcount("lbit"));
        assertEquals(jedis.bitcount("null"), lettuce.bitcount("null"));
        assertEquals(jedis.bitop(BitOP.AND, "jbit1", "jbit", "null"), lettuce.bitop(BitOP.AND, "lbit1", "lbit", "null"));
        assertEquals(jedis.bitop(BitOP.XOR, "jbit1", "jbit", "null"), lettuce.bitop(BitOP.XOR, "lbit1", "lbit", "null"));
        BitFieldArgs bitFieldArgs = BitFieldArgs.Builder.incrBy(BitFieldArgs.unsigned(8), 100, 1).get(BitFieldArgs.unsigned(4), 0);
        assertEquals(jedis.bitfield("jbit", "INCRBY", "i5", "100", "1", "GET", "u4", "0"), lettuce.bitfield("lbit", bitFieldArgs));
        lettuce.del("jbit");
        lettuce.del("lbit");
        lettuce.del("jbit1");
        lettuce.del("lbit1");
        System.out.println("bitmap 通过 !");
    }

    @Test
    public void getrange() {
        assertEquals(jedis.setrange("jsetrange", 0, "aaa"), lettuce.setrange("lsetrange", 0, "aaa"));
        assertEquals(jedis.getrange("jsetrange", 0, 1), lettuce.getrange("lsetrange", 0, 1));
        assertEquals(jedis.getrange("null", 0, 1), lettuce.getrange("null", 0, 1));
        assertEquals(jedis.pexpire("jsetrange", 1000), lettuce.pexpire("lsetrange", 1000));
        assertEquals(jedis.pexpire("null", 1000), lettuce.pexpire("null", 1000));
        assertEquals(jedis.pexpireAt("jsetrange", 1000), lettuce.pexpireAt("lsetrange", 1000));
        assertEquals(jedis.pexpireAt("null", 1000), lettuce.pexpireAt("null", 1000));
        assertEquals(jedis.pttl("jsetrange"), lettuce.pttl("lsetrange"));
        assertEquals(jedis.pttl("null"), lettuce.pttl("null"));
        assertEquals(jedis.psetex("jsetrange", 1000, "aaa"), lettuce.psetex("lsetrange", 1000, "aaa"));
        assertEquals(jedis.psetex("null", 1000, "aaa"), lettuce.psetex("null", 1000, "aaa"));
        ScanParams scanParams = new ScanParams();
        scanParams.count(2);
        scanParams.match("ge");
        assertEquals(jedis.scan("0").getResult(), lettuce.scan("0").getResult());
        assertEquals(jedis.scan("0", scanParams).getResult(), lettuce.scan("0", scanParams).getResult());
        lettuce.del("jsetrange");
        lettuce.del("lsetrange");
        System.out.println("getrange 通过 !");
    }

    @Test
    public void pubsubChannels() {
        assertEquals(jedis.pubsubChannels("*"), lettuce.pubsubChannels("*"));
        assertEquals(jedis.pubsubNumPat(), lettuce.pubsubNumPat());
        //Jedis返回HashMap，Lettuce返回LinkedhashMap
        assertEquals(jedis.pubsubNumSub("a").keySet(), lettuce.pubsubNumSub("a").keySet());
        System.out.println("pubsubChannels 通过 !");
    }

    @Test
    public void pf() {
        assertEquals(jedis.pfadd("jpf", seconds, "a", "b", "c"), lettuce.pfadd("lpf", seconds, "a", "b", "c"));
        assertEquals(jedis.pfcount("jpf"), lettuce.pfcount("lpf"));
        assertEquals(jedis.pfcount("jpf", "jpf1"), lettuce.pfcount("lpf", "lpf1"));
        //Jedis返回HashMap，Lettuce返回LinkedhashMap
        assertEquals(jedis.pfmerge("jpf2", seconds, "jpf", "jpf1"), lettuce.pfmerge("lpf2", seconds, "lpf", "lpf1"));
        lettuce.del("jpf");
        lettuce.del("lpf");
        lettuce.del("jpf1");
        lettuce.del("lpf1");
        lettuce.del("jpf2");
        lettuce.del("lpf2");
        System.out.println("pubsubChannels 通过 !");
    }

    @Test
    public void geo() {
        assertEquals(jedis.geoadd("jgeo", 10, 20, "a", seconds), lettuce.geoadd("lgeo", 10, 20, "a", seconds));
        Map<String, GeoCoordinate> memberCoordinateMap = new HashMap<>();
        memberCoordinateMap.put("a", new GeoCoordinate(10, 15));
        memberCoordinateMap.put("b", new GeoCoordinate(20, 15));
        memberCoordinateMap.put("c", new GeoCoordinate(15, 15));
        assertEquals(jedis.geoadd("jgeo", memberCoordinateMap, seconds), lettuce.geoadd("lgeo", memberCoordinateMap, seconds));
        assertEquals(jedis.geodist("jgeo", "a", "b"), lettuce.geodist("lgeo", "a", "b"));
        assertEquals(jedis.geodist("jgeo", "a", "b", GeoUnit.KM), lettuce.geodist("lgeo", "a", "b", GeoUnit.KM));
        assertEquals(jedis.geohash("jgeo", "a", "b"), lettuce.geohash("lgeo", "a", "b"));
        assertEquals(jedis.geopos("jgeo", "a", "b"), lettuce.geopos("lgeo", "a", "b"));
        assertEquals(jedis.geopos("null", "a", "b"), lettuce.geopos("null", "a", "b"));
        GeoRadiusParam param = GeoRadiusParam.geoRadiusParam().withDist().count(10).sortDescending();
        assertEquals(JSON.toJSONString(jedis.georadius("jgeo", 15, 15, 5, GeoUnit.KM, param)), JSON.toJSONString(lettuce.georadius("lgeo", 15, 15, 5, GeoUnit.KM, param)));
        assertEquals(JSON.toJSONString(jedis.georadiusByMember("jgeo", "c", 5, GeoUnit.KM, param)), JSON.toJSONString(lettuce.georadiusByMember("lgeo", "c", 5, GeoUnit.KM, param)));
        lettuce.del("jgeo");
        lettuce.del("lgeo");
        System.out.println("geo 通过 !");
    }

    private class LockConstant {

        /**
         * 释放分布式锁的lua脚本，为了维护方便
         */
        public static final String RELEASE_LOCK_SCRIPT = "if redis.call('get',KEYS[1]) == ARGV[1] then return redis.call('del',KEYS[1]) else return 0 end";

        public static final String RELEASE_LOCK_SCRIPT_NEW =
                "if redis.call('get',KEYS[1]) == ARGV[1] then " +
                        "redis.call('del',KEYS[1]) " +
                        "redis.call('publish', KEYS[2], ARGV[2]) " +
                        "return 1 " +
                        "else " +
                        "return 0 " +
                        "end";

        public static final String RELEASE_LOCK_PUB_MESSAGE = "release";
    }

    @Test
    public void eval() {
        lettuce.mset(seconds, "test1", "a", "test2", "b");
        String str1 = "return redis.call('set',KEYS[1],'aa')";//设置键k1的值为aa
        assertEquals(jedis.eval(str1, ScriptOutputType.VALUE, 1, new String[]{"test1"}), lettuce.eval(str1, ScriptOutputType.VALUE, 1, new String[]{"test1"}));
        List<String> keys = new ArrayList<>();
        List<String> args = new ArrayList<>();
        keys.add("test1");
        args.add("aa");
        assertEquals(jedis.eval(str1, ScriptOutputType.VALUE, keys, args), lettuce.eval(str1, ScriptOutputType.VALUE, keys, args));
        assertEquals(jedis.evalsha(jedis.scriptLoad(LockConstant.RELEASE_LOCK_SCRIPT), ScriptOutputType.INTEGER, 1, new String[]{"test1", "aa"}), lettuce.evalsha(jedis.scriptLoad(LockConstant.RELEASE_LOCK_SCRIPT), ScriptOutputType.INTEGER, 1, new String[]{"test2", "b"}));
        assertEquals(jedis.eval("return redis.call('get','test1')", ScriptOutputType.VALUE), lettuce.eval("return redis.call('get','test1')", ScriptOutputType.VALUE));
//        Object evalsha(String sha1, List<String> keys, List<String> args);
//        Object evalsha(String sha1, int keyCount, String... params);
        lettuce.del("test1");
        lettuce.del("test2");
        System.out.println("eval 通过 !");
    }

    private static enum TestLua implements RedisLuaInterface {
        TEST_UN_LOCK("if redis.call('get',KEYS[1]) == ARGV[1] then return redis.call('del',KEYS[1]) else return 0 end"),
        ;
        private String scripts;

        TestLua(String scripts) {
            this.scripts = scripts;
        }

        @Override
        public String getScripts() {
            return scripts;
        }
    }

    @Test
    public void testAddLua() {
        List<RedisLuaInterface> luas = new ArrayList<>();
        luas.add(TestLua.TEST_UN_LOCK);
        jedis.addLua(luas);
        lettuce.addLua(luas);
        jedis.set("test1", "test1", 60);
        jedis.set("test2", "test2", 60);
        lettuce.set("test3", "test3", 60);
        lettuce.set("test4", "test4", 60);
        assertEquals(jedis.executeByLua(TestLua.TEST_UN_LOCK, ScriptOutputType.INTEGER, Arrays.asList("test1"), Arrays.asList("test1")), lettuce.executeByLua(TestLua.TEST_UN_LOCK, ScriptOutputType.INTEGER, Arrays.asList("test3"), Arrays.asList("test3")));
        assertEquals(jedis.executeByLua(TestLua.TEST_UN_LOCK, ScriptOutputType.INTEGER, Arrays.asList("test2"), Arrays.asList("test2")), lettuce.executeByLua(TestLua.TEST_UN_LOCK, ScriptOutputType.INTEGER, Arrays.asList("test4"), Arrays.asList("test4")));
        assertNull(jedis.get("test1"));
        assertNull(jedis.get("test2"));
        assertNull(lettuce.get("test3"));
        assertNull(lettuce.get("test4"));
    }

    @Test
    public void getCacheData() {
        assertEquals(1, lettuce.getCacheData(new CacheDataBuilder()
                .setCacheGetFunction(e -> {
                    return 1;
                })
                .setDbGetFunction(e -> {
                    return 2;
                })
                .setCacheSetFunction(dbData -> {
                    return 0;
                })
                .setLockKey("getCacheDataTest")));
        assertEquals(2, lettuce.getCacheData(new CacheDataBuilder()
                .setCacheGetFunction(e -> {
                    return null;
                })
                .setDbGetFunction(e -> {
                    return 2;
                })
                .setCacheSetFunction(dbData -> {
                    return 0;
                })
                .setLockKey("getCacheDataTest")));
        assertEquals(2, lettuce.getCacheData(new CacheDataBuilder()
                .setCacheGetFunction(e -> {
                    return null;
                })
                .setDbGetFunction(e -> {
                    return 2;
                })
                .setCacheSetFunction(dbData -> {
                    return 0;
                })
                .setLockKey("getCacheDataTest")));
//        String key = "getCacheData";
//        lettuce.getCacheData(new CacheDataBuilder()
//                .setCacheGetFunction(e -> lettuce.get(key))
//                .setDbGetFunction(e -> 2)
//                .setCacheSetFunction(dbData -> {
//                    lettuce.set(key, dbData + "", 3600);
//                    return 0;
//                })
//                .setLockKey("getCacheDataTest"));
    }

    @Test
    public void evalsha() {
        String jzsetKey = "jzset:test:1";
        String lzsetKey = "lzset:test:1";
        jedis.zadd(jzsetKey, ImmutableMap.of("a", (double) 1, "b", (double) 2, "c", (double) 3), 3600);
        lettuce.zadd(lzsetKey, ImmutableMap.of("a", (double) 1, "b", (double) 2, "c", (double) 3), 3600);
        assertEquals(jedis.zaddIfKeyExists(jzsetKey, 4, "e", 3600), lettuce.zaddIfKeyExists(lzsetKey, 4, "e", 3600));
        assertEquals(jedis.zscoreBatch(jzsetKey, Arrays.asList("a", "b", "c", "e")), lettuce.zscoreBatch(lzsetKey, Arrays.asList("a", "b", "c", "e")));
        lettuce.del(jzsetKey);
        lettuce.del(lzsetKey);
        System.out.println("evalsha 通过 !");
    }
}
