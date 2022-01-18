package com.lcache.test.localCache;

import com.lcache.client.CacheClientFactory;
import com.lcache.core.BaseCacheExecutor;
import com.lcache.core.cache.localcache.RedisLocalCacheFactory;
import com.lcache.core.constant.CommandsDataTypeEnum;
import com.lcache.core.constant.ConnectTypeEnum;
import com.lcache.core.constant.RedisClientConstants;
import com.lcache.core.model.CacheConfigModel;
import com.lcache.extend.handle.localcache.StringLocalCacheHandle;
import com.lcache.extend.handle.redis.jedis.config.JedisConnectSourceConfig;
import com.lcache.extend.handle.redis.lettuce.config.LettuceConnectSourceConfig;
import com.lcache.util.CacheFunction;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.util.concurrent.ThreadLocalRandom;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class LocalCacheStringTest {

    private BaseCacheExecutor jedis = CacheClientFactory.getCacheExecutor(new CacheConfigModel().setClientType(RedisClientConstants.JEDIS).setConnectTypeEnum(ConnectTypeEnum.POOL).setCacheType("test"), new JedisConnectSourceConfig());
    private BaseCacheExecutor lettuce = CacheClientFactory.getCacheExecutor(new CacheConfigModel().setClientType(RedisClientConstants.LETTUCE).setConnectTypeEnum(ConnectTypeEnum.SIMPLE).setCacheType("test1"), new LettuceConnectSourceConfig());

    private static final int seconds = 60;

    @Test
    public void aa() {
        String s = "test", v = "abc";
        substrTest("null",null);
        substrTest("a","a");
        substrTest("ab","ab");
        substrTest("abc","abc");
        substrTest("abcd","abcd");
        substrTest("abcde","abcde");
    }

    private void substrTest(String k, String v) {
        int l = StringUtils.isBlank(v) ? 0 : v.length();
        lettuce.set(k, v, seconds);

        assertEquals(lettuce.substr(k, 0, 1), substr(v,0,1));
        assertEquals(lettuce.substr(k, 0, 2), substr(v,0,2));
        assertEquals(lettuce.substr(k, 0, 3), substr(v,0,3));
        assertEquals(lettuce.substr(k, 0, 4), substr(v,0,4));

        assertEquals(lettuce.substr(k, 0, 0), substr(v,0,0));

        assertEquals(lettuce.substr(k, 0, -1), substr(v,0,-1));
        assertEquals(lettuce.substr(k, 0, -2), substr(v,0,-2));
        assertEquals(lettuce.substr(k, 0, -3), substr(v,0,-3));
        assertEquals(lettuce.substr(k, 0, -4), substr(v,0,-4));
    }

    private String substr(String v, int s, int e) {
        return StringLocalCacheHandle.substr(v, s, e);
    }

    @Test
    public void stringTest() {
        String key = "localCacheStringTest";
        lettuce.del(key);
        buildTest(key, getBuildFunction(key, key), () -> {
            jedis.del(key);
            lettuce.del(key);
            return true;
        });
        buildTest(key, getBuildFunction(key, key), () -> {
            jedis.set(key, "a", seconds);
            lettuce.set(key, "a", seconds);
            return true;
        });
        buildTest(key, getBuildFunction(key, key), () -> {
            jedis.setex(key, seconds, "a");
            lettuce.setex(key, seconds, "a");
            return true;
        });
        buildTest(key, getBuildFunction(key, key), () -> {
            jedis.setnx(key, "a", seconds);
            lettuce.setnx(key, "a", seconds);
            return true;
        });
        buildTest(key, getBuildFunction(key, "1"), () -> {
            jedis.incr(key, seconds);
            lettuce.incr(key, seconds);
            return true;
        });
        buildTest(key, getBuildFunction(key, "2"), () -> {
            jedis.decr(key, seconds);
            lettuce.decr(key, seconds);
            return true;
        });
        buildTest(key, getBuildFunction(key, "3"), () -> {
            jedis.incrBy(key, 1, seconds);
            lettuce.incrBy(key, 1, seconds);
            return true;
        });
        buildTest(key, getBuildFunction(key, "4"), () -> {
            jedis.incrByFloat(key, 1, seconds);
            lettuce.incrByFloat(key, 1, seconds);
            return true;
        });
        buildTest(key, getBuildFunction(key, "5"), () -> {
            jedis.decrBy(key, 1, seconds);
            lettuce.decrBy(key, 1, seconds);
            return true;
        });
        buildTest(key, getBuildFunction(key, key), () -> {
            jedis.getSet(key, "test");
            lettuce.getSet(key, "test");
            return true;
        });
        buildTest(key, getBuildFunction(key, key), () -> {
            jedis.mset(seconds, key, "test", "a", "b");
            lettuce.mset(seconds, key, "test", "a", "b");
            return true;
        });
        buildTest(key, getBuildFunction(key, key), () -> {
            jedis.msetnx(seconds, key, "test", "a", "b");
            lettuce.msetnx(seconds, key, "test", "a", "b");
            return true;
        });
        buildTest(key, getBuildFunction(key, key), () -> {
            jedis.setrange(key, 5, "test");
            lettuce.setrange(key, 5, "test");
            return true;
        });
    }

    private CacheFunction getBuildFunction(String key, String value) {
        return () -> {
            jedis.set(key, value, seconds);
            lettuce.set(key, value, seconds);
            return true;
        };
    }

    public void buildTest(String key, CacheFunction build, CacheFunction setFunction) {
        /**
         * lettuce关闭，jedis关闭
         */
        jedis.closeLocalCache();
        lettuce.closeLocalCache();
        build.apply();
        get(key, setFunction);
        build.apply();
        substr(key, setFunction);
        build.apply();
        strlen(key, setFunction);
        /**
         * lettuce开启，jedis关闭
         */
        lettuce.openLocalCache().addLocalCacheKeys(key);
        jedis.closeLocalCache();
        build.apply();
        get(key, setFunction);
        build.apply();
        substr(key, setFunction);
        build.apply();
        strlen(key, setFunction);
        /**
         * lettuce关闭，jedis开启
         */
        lettuce.closeLocalCache();
        jedis.openLocalCache().addLocalCacheKeys(key);
        build.apply();
        get(key, setFunction);
        build.apply();
        substr(key, setFunction);
        build.apply();
        strlen(key, setFunction);
        /**
         * 都开启
         */
        lettuce.openLocalCache().addLocalCacheKeys(key);
        lettuce.openLocalCache().addLocalCacheKeys(key);
        build.apply();
        get(key, setFunction);
        build.apply();
        substr(key, setFunction);
        build.apply();
        strlen(key, setFunction);
    }

    /**
     * 多级缓存-字符串
     */
    public void get(String key, CacheFunction setFunction) {
        //查询
        assertEquals(jedis.get(key), lettuce.get(key));
        try {
            Thread.sleep(10L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (lettuce.getCacheConfigModel().isLocalCache() || jedis.getCacheConfigModel().isLocalCache()) {
            BaseCacheExecutor executor = lettuce.getCacheConfigModel().isLocalCache() ? lettuce : jedis;
            assertNotNull(RedisLocalCacheFactory.getLocalCacheHandle(CommandsDataTypeEnum.STRING, executor).getIfPresent(executor, key));
            assertEquals(jedis.get(key), RedisLocalCacheFactory.getLocalCacheHandle(CommandsDataTypeEnum.STRING, executor).getIfPresent(executor, key).getData());
        }
        assertEquals(jedis.get(key), lettuce.get(key));
        //删除
        setFunction.apply();
        if (lettuce.getCacheConfigModel().isLocalCache() || jedis.getCacheConfigModel().isLocalCache()) {
            BaseCacheExecutor executor = lettuce.getCacheConfigModel().isLocalCache() ? lettuce : jedis;
            assertEquals(RedisLocalCacheFactory.getLocalCacheHandle(CommandsDataTypeEnum.STRING, executor).getIfPresent(executor, key), null);
        }
        assertEquals(jedis.get(key), lettuce.get(key));
    }

    /**
     * 多级缓存-字符串
     */
    public void substr(String key, CacheFunction setFunction) {
        //查询
        assertEquals(jedis.substr(key, 0, -1), lettuce.substr(key, 0, -1));
        try {
            Thread.sleep(10L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (lettuce.getCacheConfigModel().isLocalCache() || jedis.getCacheConfigModel().isLocalCache()) {
            BaseCacheExecutor executor = lettuce.getCacheConfigModel().isLocalCache() ? lettuce : jedis;
            assertNotNull(RedisLocalCacheFactory.getLocalCacheHandle(CommandsDataTypeEnum.STRING, executor).getIfPresent(executor, key));
        }
        assertEquals(jedis.substr(key, 0, -1), RedisLocalCacheFactory.getLocalCacheHandle(CommandsDataTypeEnum.STRING, lettuce).doCacheFunc(lettuce, () -> lettuce.substr(key, 0, -1), key, new Object[]{0, -1}));
        assertEquals(jedis.substr(key, 0, -1), lettuce.substr(key, 0, -1));

        int end = ThreadLocalRandom.current().nextInt(-10, -1);
        assertEquals(jedis.substr(key, 0, end), lettuce.substr(key, 0, end));
        end = ThreadLocalRandom.current().nextInt(0, 10);
        assertEquals(jedis.substr(key, 0, end), lettuce.substr(key, 0, end));
        //删除
        setFunction.apply();
        if (lettuce.getCacheConfigModel().isLocalCache() || jedis.getCacheConfigModel().isLocalCache()) {
            BaseCacheExecutor executor = lettuce.getCacheConfigModel().isLocalCache() ? lettuce : jedis;
            assertEquals(RedisLocalCacheFactory.getLocalCacheHandle(CommandsDataTypeEnum.STRING, executor).getIfPresent(executor, key), null);
        }
        assertEquals(jedis.substr(key, 0, -1), lettuce.substr(key, 0, -1));
    }

    /**
     * 多级缓存-字符串
     */
    public void strlen(String key, CacheFunction setFunction) {
        //查询
        assertEquals(jedis.strlen(key), lettuce.strlen(key));
        try {
            Thread.sleep(10L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (lettuce.getCacheConfigModel().isLocalCache() || jedis.getCacheConfigModel().isLocalCache()) {
            BaseCacheExecutor executor = lettuce.getCacheConfigModel().isLocalCache() ? lettuce : jedis;
            assertNotNull(RedisLocalCacheFactory.getLocalCacheHandle(CommandsDataTypeEnum.STRING, executor).getIfPresent(executor, key));
        }
        assertEquals(jedis.strlen(key), RedisLocalCacheFactory.getLocalCacheHandle(CommandsDataTypeEnum.STRING, lettuce).doCacheFunc(lettuce, () -> lettuce.strlen(key), key, null));
        assertEquals(jedis.strlen(key), lettuce.strlen(key));
        //删除
        setFunction.apply();
        if (lettuce.getCacheConfigModel().isLocalCache() || jedis.getCacheConfigModel().isLocalCache()) {
            BaseCacheExecutor executor = lettuce.getCacheConfigModel().isLocalCache() ? lettuce : jedis;
            assertEquals(RedisLocalCacheFactory.getLocalCacheHandle(CommandsDataTypeEnum.STRING, executor).getIfPresent(executor, key), null);
        }
        assertEquals(jedis.strlen(key), lettuce.strlen(key));
    }
}
