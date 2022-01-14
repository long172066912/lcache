package com.lcache.test.localCache;

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

import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class LocalCacheSetTest {

    private BaseCacheExecutor jedis = CacheClientFactory.getCacheExecutor(new CacheConfigModel().setClientType(RedisClientConstants.JEDIS).setConnectTypeEnum(ConnectTypeEnum.POOL).setCacheType("test"), new JedisConnectSourceConfig());
    private BaseCacheExecutor lettuce = CacheClientFactory.getCacheExecutor(new CacheConfigModel().setClientType(RedisClientConstants.LETTUCE).setConnectTypeEnum(ConnectTypeEnum.SIMPLE).setCacheType("test1"), new LettuceConnectSourceConfig());

    private static final int seconds = 60;

    @Test
    public void hashTest() {
        String key = "localCacheSetTest";
        String member = "a";
        buildTest(key, member, getBuildFunction(key), () -> {
            jedis.sadd(key, "aaa", seconds);
            lettuce.sadd(key, "aaa", seconds);
            return true;
        });
        buildTest(key, member, getBuildFunction(key), () -> {
            jedis.smove(key,key + "test", "a");
            lettuce.smove(key,key + "test", "a");
            return true;
        });
        buildTest(key, member, getBuildFunction(key), () -> {
            jedis.spop(key, 5);
            lettuce.spop(key, 5);
            return true;
        });
        buildTest(key, member, getBuildFunction(key), () -> {
            jedis.srem(key, "a");
            lettuce.srem(key, "a");
            return true;
        });
    }

    private CacheFunction getBuildFunction(String key) {
        return () -> {
            jedis.sadd(key, new String[]{"a","b","c"}, seconds);
            lettuce.sadd(key, new String[]{"a","b","c"}, seconds);
            return true;
        };
    }

    public void buildTest(String key, String member, CacheFunction build, CacheFunction setFunction) {
        /**
         * lettuce关闭，jedis关闭
         */
        jedis.closeLocalCache();
        lettuce.closeLocalCache();
        build.apply();
        sismember(key, member, setFunction);
        build.apply();
        smembers(key, setFunction);
        build.apply();
        scard(key, setFunction);
        /**
         * lettuce开启，jedis关闭
         */
        lettuce.openLocalCache().addLocalCacheKeys(key);
        jedis.closeLocalCache();
        build.apply();
        sismember(key, member, setFunction);
        build.apply();
        smembers(key, setFunction);
        build.apply();
        scard(key, setFunction);
        /**
         * lettuce关闭，jedis开启
         */
        lettuce.closeLocalCache();
        jedis.openLocalCache().addLocalCacheKeys(key);
        build.apply();
        sismember(key, member, setFunction);
        build.apply();
        smembers(key, setFunction);
        build.apply();
        scard(key, setFunction);
        /**
         * 都开启
         */
        lettuce.openLocalCache().addLocalCacheKeys(key);
        lettuce.openLocalCache().addLocalCacheKeys(key);
        build.apply();
        sismember(key, member, setFunction);
        build.apply();
        smembers(key, setFunction);
        build.apply();
        scard(key, setFunction);
    }

    /**
     * 多级缓存-sismember-jedis不开启，lettuce开启
     */
    public void sismember(String key, String member, CacheFunction setFunction) {
        //查询
        assertEquals(jedis.sismember(key, member), lettuce.sismember(key, member));
        try {
            Thread.sleep(10L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (lettuce.getCacheConfigModel().isLocalCache() || jedis.getCacheConfigModel().isLocalCache()) {
            BaseCacheExecutor executor = lettuce.getCacheConfigModel().isLocalCache() ? lettuce : jedis;
            assertNotNull(RedisLocalCacheFactory.getLocalCacheHandle(CommandsDataTypeEnum.HASH, executor).getIfPresent(executor, key));
            assertEquals(jedis.sismember(key, member), ((Set) RedisLocalCacheFactory.getLocalCacheHandle(CommandsDataTypeEnum.SET, executor).getIfPresent(executor, key).getData()).contains(member));
        }
        assertEquals(jedis.sismember(key, member), lettuce.sismember(key, member));
        //删除
        setFunction.apply();
        if (lettuce.getCacheConfigModel().isLocalCache() || jedis.getCacheConfigModel().isLocalCache()) {
            BaseCacheExecutor executor = lettuce.getCacheConfigModel().isLocalCache() ? lettuce : jedis;
            assertEquals(RedisLocalCacheFactory.getLocalCacheHandle(CommandsDataTypeEnum.HASH, executor).getIfPresent(executor, key), null);
        }
        assertEquals(jedis.sismember(key, member), lettuce.sismember(key, member));
    }

    /**
     * 多级缓存-sismember-jedis不开启，lettuce开启
     */
    public void smembers(String key, CacheFunction setFunction) {
        //查询
        assertEquals(jedis.smembers(key), lettuce.smembers(key));
        try {
            Thread.sleep(10L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (lettuce.getCacheConfigModel().isLocalCache() || jedis.getCacheConfigModel().isLocalCache()) {
            BaseCacheExecutor executor = lettuce.getCacheConfigModel().isLocalCache() ? lettuce : jedis;
            assertNotNull(RedisLocalCacheFactory.getLocalCacheHandle(CommandsDataTypeEnum.HASH, executor).getIfPresent(executor, key));
            assertEquals(jedis.smembers(key), RedisLocalCacheFactory.getLocalCacheHandle(CommandsDataTypeEnum.SET, executor).getIfPresent(executor, key).getData());
        }
        assertEquals(jedis.smembers(key), lettuce.smembers(key));
        //删除
        setFunction.apply();
        if (lettuce.getCacheConfigModel().isLocalCache() || jedis.getCacheConfigModel().isLocalCache()) {
            BaseCacheExecutor executor = lettuce.getCacheConfigModel().isLocalCache() ? lettuce : jedis;
            assertEquals(RedisLocalCacheFactory.getLocalCacheHandle(CommandsDataTypeEnum.HASH, executor).getIfPresent(executor, key), null);
        }
        assertEquals(jedis.smembers(key), lettuce.smembers(key));
    }

    /**
     * 多级缓存-sismember-jedis不开启，lettuce开启
     */
    public void scard(String key, CacheFunction setFunction) {
        //查询
        assertEquals(jedis.scard(key), lettuce.scard(key));
        try {
            Thread.sleep(10L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (lettuce.getCacheConfigModel().isLocalCache() || jedis.getCacheConfigModel().isLocalCache()) {
            BaseCacheExecutor executor = lettuce.getCacheConfigModel().isLocalCache() ? lettuce : jedis;
            assertNotNull(RedisLocalCacheFactory.getLocalCacheHandle(CommandsDataTypeEnum.HASH, executor).getIfPresent(executor, key));
            assertEquals(jedis.scard(key).intValue(), ((Set) RedisLocalCacheFactory.getLocalCacheHandle(CommandsDataTypeEnum.SET, executor).getIfPresent(executor, key).getData()).size());
        }
        assertEquals(jedis.scard(key), lettuce.scard(key));
        //删除
        setFunction.apply();
        if (lettuce.getCacheConfigModel().isLocalCache() || jedis.getCacheConfigModel().isLocalCache()) {
            BaseCacheExecutor executor = lettuce.getCacheConfigModel().isLocalCache() ? lettuce : jedis;
            assertEquals(RedisLocalCacheFactory.getLocalCacheHandle(CommandsDataTypeEnum.HASH, executor).getIfPresent(executor, key), null);
        }
        assertEquals(jedis.scard(key), lettuce.scard(key));
    }
}
