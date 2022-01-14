package com.lcache.test.localCache;

import com.google.common.collect.ImmutableMap;
import com.lcache.client.CacheClientFactory;
import com.lcache.core.BaseCacheExecutor;
import com.lcache.core.cache.localcache.RedisLocalCacheFactory;
import com.lcache.core.constant.CommandsDataTypeEnum;
import com.lcache.core.constant.ConnectTypeEnum;
import com.lcache.core.constant.RedisClientConstants;
import com.lcache.core.model.CacheConfigModel;
import com.lcache.extend.handle.redis.jedis.config.JedisConnectSourceConfig;
import com.lcache.extend.handle.redis.lettuce.config.LettuceConnectSourceConfig;
import com.lcache.util.CacheFunction;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class LocalCacheHashTest {

    private BaseCacheExecutor jedis = CacheClientFactory.getCacheExecutor(new CacheConfigModel().setClientType(RedisClientConstants.JEDIS).setConnectTypeEnum(ConnectTypeEnum.POOL).setCacheType("test"), new JedisConnectSourceConfig());
    private BaseCacheExecutor lettuce = CacheClientFactory.getCacheExecutor(new CacheConfigModel().setClientType(RedisClientConstants.LETTUCE).setConnectTypeEnum(ConnectTypeEnum.SIMPLE).setCacheType("test1"), new LettuceConnectSourceConfig());

    private static final int seconds = 60;

    @Test
    public void hashTest() {
        String key = "localCacheHashTest";
        lettuce.del(key);
        String field = "a";
        String[] fields = new String[]{"a", "b", "c"};
        Map<String, String> map = ImmutableMap.of("aa", "Aa", "a", "A", "b", "B", "c", "C", "d", "D");
        buildTest(key, field, fields, getBuildFunction(key, map), () -> {
            jedis.hset(key, field, "aaa", seconds);
            lettuce.hset(key, field, "aaa", seconds);
            return true;
        });
        buildTest(key, field, fields, getBuildFunction(key, map), () -> {
            jedis.hsetnx(key, field, "aaa", seconds);
            lettuce.hsetnx(key, field, "aaa", seconds);
            return true;
        });
        buildTest(key, field, fields, getBuildFunction(key, map), () -> {
            jedis.hdel(key, field);
            lettuce.hdel(key, field);
            return true;
        });
        buildTest(key, field, fields, getBuildFunction(key, map), () -> {
            lettuce.hset(key, field, "1", seconds);
            jedis.hincrBy(key, field, 1, seconds);
            lettuce.hincrBy(key, field, 1, seconds);
            return true;
        });
        buildTest(key, field, fields, getBuildFunction(key, map), () -> {
            lettuce.hset(key, field, "1", seconds);
            jedis.hincrByFloat(key, field, 2, seconds);
            lettuce.hincrByFloat(key, field, 2, seconds);
            return true;
        });
        buildTest(key, field, fields, getBuildFunction(key, map), () -> {
            jedis.hmset(key, ImmutableMap.of("c", "C", "d", "D"), seconds);
            lettuce.hmset(key, ImmutableMap.of("c", "C", "d", "D"), seconds);
            return true;
        });
    }

    private CacheFunction getBuildFunction(String key, Map<String, String> vals) {
        return () -> {
            jedis.hmset(key, vals, seconds);
            lettuce.hmset(key, vals, seconds);
            return true;
        };
    }

    public void buildTest(String key, String field, String[] fields, CacheFunction build, CacheFunction setFunction) {
        /**
         * lettuce关闭，jedis关闭
         */
        jedis.closeLocalCache();
        lettuce.closeLocalCache();
        build.apply();
        hget(key, field, setFunction);
        build.apply();
        hexists(key, field, setFunction);
        build.apply();
        hmget(key, fields, setFunction);
        build.apply();
        hmgetToMap(key, fields, setFunction);
        build.apply();
        hmgetToMapCanNull(key, fields, setFunction);
        build.apply();
        hgetall(key, setFunction);
        build.apply();
        hkeys(key, setFunction);
        build.apply();
        hlen(key, setFunction);
        build.apply();
        hvals(key, setFunction);
        /**
         * lettuce开启，jedis关闭
         */
        lettuce.openLocalCache().addLocalCacheKeys(key);
        jedis.closeLocalCache();
        build.apply();
        hget(key, field, setFunction);
        build.apply();
        hexists(key, field, setFunction);
        build.apply();
        hmget(key, fields, setFunction);
        build.apply();
        hmgetToMap(key, fields, setFunction);
        build.apply();
        hmgetToMapCanNull(key, fields, setFunction);
        build.apply();
        hgetall(key, setFunction);
        build.apply();
        hkeys(key, setFunction);
        build.apply();
        hlen(key, setFunction);
        build.apply();
        hvals(key, setFunction);
        /**
         * lettuce关闭，jedis开启
         */
        lettuce.closeLocalCache();
        jedis.openLocalCache().addLocalCacheKeys(key);
        build.apply();
        hget(key, field, setFunction);
        build.apply();
        hexists(key, field, setFunction);
        build.apply();
        hmget(key, fields, setFunction);
        build.apply();
        hmgetToMap(key, fields, setFunction);
        build.apply();
        hmgetToMapCanNull(key, fields, setFunction);
        build.apply();
        hgetall(key, setFunction);
        build.apply();
        hkeys(key, setFunction);
        build.apply();
        hlen(key, setFunction);
        build.apply();
        hvals(key, setFunction);
        /**
         * 都开启
         */
        lettuce.openLocalCache().addLocalCacheKeys(key);
        lettuce.openLocalCache().addLocalCacheKeys(key);
        build.apply();
        hget(key, field, setFunction);
        build.apply();
        hexists(key, field, setFunction);
        build.apply();
        hmget(key, fields, setFunction);
        build.apply();
        hmgetToMap(key, fields, setFunction);
        build.apply();
        hmgetToMapCanNull(key, fields, setFunction);
        build.apply();
        hgetall(key, setFunction);
        build.apply();
        hkeys(key, setFunction);
        build.apply();
        hlen(key, setFunction);
        build.apply();
        hvals(key, setFunction);
    }

    /**
     * 多级缓存-hget-jedis不开启，lettuce开启
     */
    public void hget(String key, String field, CacheFunction setFunction) {
        //查询
        assertEquals(jedis.hget(key, field), lettuce.hget(key, field));
        try {
            Thread.sleep(10L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (lettuce.getCacheConfigModel().isLocalCache() || jedis.getCacheConfigModel().isLocalCache()) {
            BaseCacheExecutor executor = lettuce.getCacheConfigModel().isLocalCache() ? lettuce : jedis;
            assertNotNull(RedisLocalCacheFactory.getLocalCacheHandle(CommandsDataTypeEnum.HASH, executor).getIfPresent(executor, key));
            assertEquals(jedis.hget(key, field), ((Map) RedisLocalCacheFactory.getLocalCacheHandle(CommandsDataTypeEnum.HASH, executor).getIfPresent(executor, key).getData()).get(field));
        }
        assertEquals(jedis.hget(key, field), lettuce.hget(key, field));
        //删除
        setFunction.apply();
        if (lettuce.getCacheConfigModel().isLocalCache() || jedis.getCacheConfigModel().isLocalCache()) {
            BaseCacheExecutor executor = lettuce.getCacheConfigModel().isLocalCache() ? lettuce : jedis;
            assertEquals(RedisLocalCacheFactory.getLocalCacheHandle(CommandsDataTypeEnum.HASH, executor).getIfPresent(executor, key), null);
        }
        assertEquals(jedis.hget(key, field), lettuce.hget(key, field));
    }

    /**
     * 多级缓存-hmget-jedis不开启，lettuce开启
     */
    public void hmget(String key, String[] field, CacheFunction setFunction) {
        //查询
        assertEquals(jedis.hmget(key, field), lettuce.hmget(key, field));
        try {
            Thread.sleep(10L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (lettuce.getCacheConfigModel().isLocalCache() || jedis.getCacheConfigModel().isLocalCache()) {
            BaseCacheExecutor executor = lettuce.getCacheConfigModel().isLocalCache() ? lettuce : jedis;
            assertNotNull(RedisLocalCacheFactory.getLocalCacheHandle(CommandsDataTypeEnum.HASH, executor).getIfPresent(executor, key));
            Arrays.stream(field).forEach(e -> assertEquals(jedis.hget(key, e), ((Map) RedisLocalCacheFactory.getLocalCacheHandle(CommandsDataTypeEnum.HASH, executor).getIfPresent(executor, key).getData()).get(e)));
        }
        assertEquals(jedis.hmget(key, field), lettuce.hmget(key, field));
        //删除
        setFunction.apply();
        if (lettuce.getCacheConfigModel().isLocalCache() || jedis.getCacheConfigModel().isLocalCache()) {
            BaseCacheExecutor executor = lettuce.getCacheConfigModel().isLocalCache() ? lettuce : jedis;
            assertEquals(RedisLocalCacheFactory.getLocalCacheHandle(CommandsDataTypeEnum.HASH, executor).getIfPresent(executor, key), null);
        }
        assertEquals(jedis.hmget(key, field), lettuce.hmget(key, field));
    }

    /**
     * 多级缓存-hmgetToMap-jedis不开启，lettuce开启
     */
    public void hmgetToMap(String key, String[] field, CacheFunction setFunction) {
        //查询
        assertEquals(jedis.hmgetToMap(key, field), lettuce.hmgetToMap(key, field));
        try {
            Thread.sleep(10L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (lettuce.getCacheConfigModel().isLocalCache() || jedis.getCacheConfigModel().isLocalCache()) {
            BaseCacheExecutor executor = lettuce.getCacheConfigModel().isLocalCache() ? lettuce : jedis;
            assertNotNull(RedisLocalCacheFactory.getLocalCacheHandle(CommandsDataTypeEnum.HASH, executor).getIfPresent(executor, key));
            Arrays.stream(field).forEach(e -> assertEquals(jedis.hget(key, e), ((Map) RedisLocalCacheFactory.getLocalCacheHandle(CommandsDataTypeEnum.HASH, executor).getIfPresent(executor, key).getData()).get(e)));
        }
        assertEquals(jedis.hmgetToMap(key, field), lettuce.hmgetToMap(key, field));
        //删除
        setFunction.apply();
        if (lettuce.getCacheConfigModel().isLocalCache() || jedis.getCacheConfigModel().isLocalCache()) {
            BaseCacheExecutor executor = lettuce.getCacheConfigModel().isLocalCache() ? lettuce : jedis;
            assertEquals(RedisLocalCacheFactory.getLocalCacheHandle(CommandsDataTypeEnum.HASH, executor).getIfPresent(executor, key), null);
        }
        assertEquals(jedis.hmgetToMap(key, field), lettuce.hmgetToMap(key, field));
    }

    /**
     * 多级缓存-hmgetToMapCanNull-jedis不开启，lettuce开启
     */
    public void hmgetToMapCanNull(String key, String[] field, CacheFunction setFunction) {
        //查询
        assertEquals(jedis.hmgetToMapCanNull(key, field), lettuce.hmgetToMapCanNull(key, field));
        try {
            Thread.sleep(10L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (lettuce.getCacheConfigModel().isLocalCache() || jedis.getCacheConfigModel().isLocalCache()) {
            BaseCacheExecutor executor = lettuce.getCacheConfigModel().isLocalCache() ? lettuce : jedis;
            assertNotNull(RedisLocalCacheFactory.getLocalCacheHandle(CommandsDataTypeEnum.HASH, executor).getIfPresent(executor, key));
            Arrays.stream(field).forEach(e -> assertEquals(jedis.hget(key, e), ((Map) RedisLocalCacheFactory.getLocalCacheHandle(CommandsDataTypeEnum.HASH, executor).getIfPresent(executor, key).getData()).get(e)));
        }
        assertEquals(jedis.hmgetToMapCanNull(key, field), lettuce.hmgetToMapCanNull(key, field));
        //删除
        setFunction.apply();
        if (lettuce.getCacheConfigModel().isLocalCache() || jedis.getCacheConfigModel().isLocalCache()) {
            BaseCacheExecutor executor = lettuce.getCacheConfigModel().isLocalCache() ? lettuce : jedis;
            assertEquals(RedisLocalCacheFactory.getLocalCacheHandle(CommandsDataTypeEnum.HASH, executor).getIfPresent(executor, key), null);
        }
        assertEquals(jedis.hmgetToMapCanNull(key, field), lettuce.hmgetToMapCanNull(key, field));
    }

    /**
     * 多级缓存-hgetall-jedis不开启，lettuce开启
     */
    public void hgetall(String key, CacheFunction setFunction) {
        //查询
        assertEquals(jedis.hgetAll(key), lettuce.hgetAll(key));
        try {
            Thread.sleep(10L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (lettuce.getCacheConfigModel().isLocalCache() || jedis.getCacheConfigModel().isLocalCache()) {
            BaseCacheExecutor executor = lettuce.getCacheConfigModel().isLocalCache() ? lettuce : jedis;
            assertNotNull(RedisLocalCacheFactory.getLocalCacheHandle(CommandsDataTypeEnum.HASH, executor).getIfPresent(executor, key));
            assertEquals(jedis.hgetAll(key), RedisLocalCacheFactory.getLocalCacheHandle(CommandsDataTypeEnum.HASH, executor).getIfPresent(executor, key).getData());
        }
        assertEquals(jedis.hgetAll(key), lettuce.hgetAll(key));
        //删除
        setFunction.apply();
        if (lettuce.getCacheConfigModel().isLocalCache() || jedis.getCacheConfigModel().isLocalCache()) {
            BaseCacheExecutor executor = lettuce.getCacheConfigModel().isLocalCache() ? lettuce : jedis;
            assertEquals(RedisLocalCacheFactory.getLocalCacheHandle(CommandsDataTypeEnum.HASH, executor).getIfPresent(executor, key), null);
        }
        assertEquals(jedis.hgetAll(key), lettuce.hgetAll(key));
    }

    /**
     * 多级缓存-hkeys-jedis不开启，lettuce开启
     */
    public void hkeys(String key, CacheFunction setFunction) {
        //查询
        assertEquals(jedis.hkeys(key), lettuce.hkeys(key));
        try {
            Thread.sleep(10L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (lettuce.getCacheConfigModel().isLocalCache() || jedis.getCacheConfigModel().isLocalCache()) {
            BaseCacheExecutor executor = lettuce.getCacheConfigModel().isLocalCache() ? lettuce : jedis;
            assertNotNull(RedisLocalCacheFactory.getLocalCacheHandle(CommandsDataTypeEnum.HASH, executor).getIfPresent(executor, key));
            assertEquals(jedis.hkeys(key), ((Map) RedisLocalCacheFactory.getLocalCacheHandle(CommandsDataTypeEnum.HASH, executor).getIfPresent(executor, key).getData()).keySet());
        }
        assertEquals(jedis.hkeys(key), lettuce.hkeys(key));
        //删除
        setFunction.apply();
        if (lettuce.getCacheConfigModel().isLocalCache() || jedis.getCacheConfigModel().isLocalCache()) {
            BaseCacheExecutor executor = lettuce.getCacheConfigModel().isLocalCache() ? lettuce : jedis;
            assertEquals(RedisLocalCacheFactory.getLocalCacheHandle(CommandsDataTypeEnum.HASH, executor).getIfPresent(executor, key), null);
        }
        assertEquals(jedis.hkeys(key), lettuce.hkeys(key));
    }

    /**
     * 多级缓存-hkeys-jedis不开启，lettuce开启
     */
    public void hlen(String key, CacheFunction setFunction) {
        //查询
        assertEquals(jedis.hlen(key), lettuce.hlen(key));
        try {
            Thread.sleep(10L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (lettuce.getCacheConfigModel().isLocalCache() || jedis.getCacheConfigModel().isLocalCache()) {
            BaseCacheExecutor executor = lettuce.getCacheConfigModel().isLocalCache() ? lettuce : jedis;
            assertNotNull(RedisLocalCacheFactory.getLocalCacheHandle(CommandsDataTypeEnum.HASH, executor).getIfPresent(executor, key));
            assertEquals(jedis.hlen(key).intValue(), ((Map) RedisLocalCacheFactory.getLocalCacheHandle(CommandsDataTypeEnum.HASH, executor).getIfPresent(executor, key).getData()).size());
        }
        assertEquals(jedis.hlen(key), lettuce.hlen(key));
        //删除
        setFunction.apply();
        if (lettuce.getCacheConfigModel().isLocalCache() || jedis.getCacheConfigModel().isLocalCache()) {
            BaseCacheExecutor executor = lettuce.getCacheConfigModel().isLocalCache() ? lettuce : jedis;
            assertEquals(RedisLocalCacheFactory.getLocalCacheHandle(CommandsDataTypeEnum.HASH, executor).getIfPresent(executor, key), null);
        }
        assertEquals(jedis.hlen(key), lettuce.hlen(key));
    }

    /**
     * 多级缓存-hkeys-jedis不开启，lettuce开启
     */
    public void hvals(String key, CacheFunction setFunction) {
        //查询
        assertEquals(jedis.hvals(key), lettuce.hvals(key));
        try {
            Thread.sleep(10L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (lettuce.getCacheConfigModel().isLocalCache() || jedis.getCacheConfigModel().isLocalCache()) {
            BaseCacheExecutor executor = lettuce.getCacheConfigModel().isLocalCache() ? lettuce : jedis;
            assertNotNull(RedisLocalCacheFactory.getLocalCacheHandle(CommandsDataTypeEnum.HASH, executor).getIfPresent(executor, key));
            assertEquals(new HashSet<>(jedis.hvals(key)), new HashSet<>(((Map) RedisLocalCacheFactory.getLocalCacheHandle(CommandsDataTypeEnum.HASH, executor).getIfPresent(executor, key).getData()).values()));
        }
        assertEquals(new HashSet<>(jedis.hvals(key)), new HashSet<>(lettuce.hvals(key)));
        //删除
        setFunction.apply();
        if (lettuce.getCacheConfigModel().isLocalCache() || jedis.getCacheConfigModel().isLocalCache()) {
            BaseCacheExecutor executor = lettuce.getCacheConfigModel().isLocalCache() ? lettuce : jedis;
            assertEquals(RedisLocalCacheFactory.getLocalCacheHandle(CommandsDataTypeEnum.HASH, executor).getIfPresent(executor, key), null);
        }
        assertEquals(jedis.hvals(key), lettuce.hvals(key));
    }

    /**
     * 多级缓存-hget-jedis不开启，lettuce开启
     */
    public void hexists(String key, String field, CacheFunction setFunction) {
        //查询
        assertEquals(jedis.hexists(key, field), lettuce.hexists(key, field));
        try {
            Thread.sleep(10L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (lettuce.getCacheConfigModel().isLocalCache() || jedis.getCacheConfigModel().isLocalCache()) {
            BaseCacheExecutor executor = lettuce.getCacheConfigModel().isLocalCache() ? lettuce : jedis;
            assertNotNull(RedisLocalCacheFactory.getLocalCacheHandle(CommandsDataTypeEnum.HASH, executor).getIfPresent(executor, key));
            assertEquals(jedis.hexists(key, field), ((Map) RedisLocalCacheFactory.getLocalCacheHandle(CommandsDataTypeEnum.HASH, executor).getIfPresent(executor, key).getData()).containsKey(field));
        }
        assertEquals(jedis.hexists(key, field), lettuce.hexists(key, field));
        //删除
        setFunction.apply();
        if (lettuce.getCacheConfigModel().isLocalCache() || jedis.getCacheConfigModel().isLocalCache()) {
            BaseCacheExecutor executor = lettuce.getCacheConfigModel().isLocalCache() ? lettuce : jedis;
            assertEquals(RedisLocalCacheFactory.getLocalCacheHandle(CommandsDataTypeEnum.HASH, executor).getIfPresent(executor, key), null);
        }
        assertEquals(jedis.hexists(key, field), lettuce.hexists(key, field));
    }
}
