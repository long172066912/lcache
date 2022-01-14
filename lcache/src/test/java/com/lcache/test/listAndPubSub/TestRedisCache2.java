package com.lcache.test.listAndPubSub;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.ImmutableMap;
import com.lcache.client.CacheClientFactory;
import com.lcache.core.BaseCacheExecutor;
import com.lcache.core.cache.localcache.LcacheCaffeineLocalCache;
import com.lcache.core.model.CacheConfigModel;
import com.lcache.extend.handle.pipeline.PipelineCmd;
import com.lcache.extend.handle.pipeline.PipelineGet;
import com.lcache.extend.handle.pipeline.PipelineZremRangeByScore;
import com.lcache.extend.handle.redis.jedis.config.JedisConnectSourceConfig;
import com.lcache.extend.handle.redis.lettuce.config.LettuceConnectSourceConfig;
import io.lettuce.core.api.StatefulConnection;
import org.junit.Test;
import org.redisson.api.RLock;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class TestRedisCache2 {

    @Test
    public void testLettuceCluster() {
//        LettuceClusterConnectSourceConfig lettuceClusterConnectSourceConfig = new LettuceClusterConnectSourceConfig();
//        lettuceClusterConnectSourceConfig.setNodes(new HashSet<>());
//        lettuceClusterConnectSourceConfig.getNodes().add(new LettuceConnectSourceConfig("127.0.0.1",30001,null,1500));
//        lettuceClusterConnectSourceConfig.getNodes().add(new LettuceConnectSourceConfig("127.0.0.1",30002,null,1500));
//        lettuceClusterConnectSourceConfig.getNodes().add(new LettuceConnectSourceConfig("127.0.0.1",30003,null,1500));
//        lettuceClusterConnectSourceConfig.getNodes().add(new LettuceConnectSourceConfig("127.0.0.1",30004,null,1500));
//        lettuceClusterConnectSourceConfig.getNodes().add(new LettuceConnectSourceConfig("127.0.0.1",30005,null,1500));
//        lettuceClusterConnectSourceConfig.getNodes().add(new LettuceConnectSourceConfig("127.0.0.1",30006,null,1500));
        BaseCacheExecutor baseCacheExecutor = CacheClientFactory.getCacheExecutor(CacheConfigModel.lettucePool("test"), new LettuceConnectSourceConfig());

        new Thread(() -> {
            while (true) {
                for (int i = 0; i < 100; i++) {
                    try {
                        baseCacheExecutor.setex("test" + i, 86400, i + "");
                        System.out.println("写入" + i);
                        Thread.sleep(10L);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        new Thread(() -> {
            while (true) {
                for (int i = 0; i < 100; i++) {
                    try {
                        System.out.println("读取" + baseCacheExecutor.get("test" + i));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
        while (true) {

        }
    }

    @Test
    public void testNewCache() {
        //从Apollo获取配置
        BaseCacheExecutor baseCacheExecutor = CacheClientFactory.getApolloCacheExecutor("club");
        for (int i = 0; i < 1000; i++) {
            baseCacheExecutor.setex("test" + i, 86400, i + "");
            System.out.println("写入" + i);
            try {
                Thread.sleep(500L);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        for (int i = 0; i < 1000; i++) {
            try {
                System.out.println("读取" + baseCacheExecutor.get("test" + i));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        while (true) {

        }
    }

    private static int getRandomIntInRange(int min, int max) {
        return ThreadLocalRandom.current().ints(min, (max + 1)).limit(1).findFirst().getAsInt();
    }

    @Test
    public void testLock() {
        BaseCacheExecutor baseCacheExecutor = CacheClientFactory.getCacheExecutor(CacheConfigModel.lettuce("club"));
        //多线程测试
        Thread a = new Thread(() -> {
            while (true) {
                int i = 1;
                RLock lock = baseCacheExecutor.lock("lock" + i, 1, TimeUnit.SECONDS);
                try {
                    if (null != lock) {
                        System.out.println(i + "a加锁成功");
                    }
                    Thread.sleep(2000L);
                    System.out.println("a 执行完毕" + i);
                } catch (Exception e) {
                } finally {
                    baseCacheExecutor.unLock(lock);
                    System.out.println(i + "a解锁成功");
                }
            }
        });

        //多线程测试
        Thread b = new Thread(() -> {
            while (true) {
                int i = 1;
                RLock lock = baseCacheExecutor.lock("lock" + i, 10, TimeUnit.SECONDS);
                try {
                    if (null != lock) {
                        System.out.println(i + "b加锁成功");
                    }
                    Thread.sleep(500L);
                    System.out.println("b 执行完毕" + i);
                } catch (Exception e) {
                } finally {
                    baseCacheExecutor.unLock(lock);
                    System.out.println(i + "b解锁成功");
                }
            }
        });

        a.start();
        b.start();

        try {
            a.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void testList() {
        BaseCacheExecutor baseCacheExecutor = CacheClientFactory.getCacheExecutor(CacheConfigModel.lettucePool("test"), new LettuceConnectSourceConfig());
        Thread a = new Thread(() -> {
            int i = 0;
            while (true) {
                i++;
                System.out.println("Lettuce发布消息：" + i);
                try {
                    baseCacheExecutor.lpush("testList", "test" + i, 3600);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    Thread.sleep(1000L);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        Thread b = new Thread(() -> {
            while (true) {
                System.out.println(baseCacheExecutor.brpop(1, "testList"));
            }
        });
        new Thread(() -> {
            while (true) {
                System.out.println(baseCacheExecutor.get("test"));
            }
        }).start();
        a.start();
        b.start();

        try {
            a.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testPubSub() {

        for (int p = 0; p < 10; p++) {
            int finalP = p;
            new Thread(() -> {
                BaseCacheExecutor baseCacheExecutor = null;
                if (finalP % 2 == 0) {
                    baseCacheExecutor = CacheClientFactory.getCacheExecutor(CacheConfigModel.newCache("test"), new LettuceConnectSourceConfig());
                } else {
                    baseCacheExecutor = CacheClientFactory.getCacheExecutor(CacheConfigModel.newCache("friend"));
                }
                int i = 0;
                while (true) {
                    i++;
                    System.out.println("Lettuce发布消息：" + i);
                    try {
                        baseCacheExecutor.publish("test", "test" + i);
                        Thread.sleep(1000L);
                    } catch (Exception e) {
                        e.printStackTrace();
                        StatefulConnection connectResource = (StatefulConnection) baseCacheExecutor.getConnectResource();
                        if (!connectResource.isOpen()) {
                            System.out.println("关闭连接" + connectResource);
                            baseCacheExecutor.close();
                        }
                        baseCacheExecutor.returnConnectResource();
                    }
                }
            }).start();
        }


        new Thread(() -> {
            BaseCacheExecutor baseCacheExecutor = null;
            baseCacheExecutor = CacheClientFactory.getCacheExecutor(CacheConfigModel.newCache("test"), new LettuceConnectSourceConfig());
            baseCacheExecutor.subscribe((message) -> {
                System.out.println("Lettuce Local订阅消息：" + message);
            }, "test");
        }).start();
        new Thread(() -> {
            BaseCacheExecutor baseCacheExecutor = null;
            baseCacheExecutor = CacheClientFactory.getCacheExecutor(CacheConfigModel.newCache("friend"));
            baseCacheExecutor.subscribe((message) -> {
                System.out.println("Lettuce Dev订阅消息：" + message);
            }, "test");
        }).start();


        while (true) {

        }
    }

    @Test
    public void testPipeline() {
        BaseCacheExecutor baseCacheExecutor = CacheClientFactory.getCacheExecutor("friend", new LettuceConnectSourceConfig());
//        while (true){
        List<PipelineCmd> commands = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            commands.add(new PipelineGet("test"));
        }
        commands.add(new PipelineZremRangeByScore("test111", 0, System.currentTimeMillis()));
        //同步方式
        List<Object> resList = baseCacheExecutor.pSync(commands);
        System.out.println(JSON.toJSONString(resList));
        //异步方式
        CompletableFuture<List<Object>> listCompletableFuture = baseCacheExecutor.pAsync(commands);
//        }
    }

    @Test
    public void testLocalCache() {
        BaseCacheExecutor baseCacheExecutor = CacheClientFactory.getCacheExecutor(CacheConfigModel.lettucePool("friend"));
        /**
         * 获取本地缓存中的值，如果拿不到则会调用baseCacheExecutor.get(key)获取，并存入本地缓存，有效期60s
         */
        baseCacheExecutor.localGetAndSet("test123");
        /**
         * 获取本地缓存中的值，如果拿不到则会调用第二个参数中的function获取，并存入本地缓存，有效期60s
         */
        baseCacheExecutor.localGetAndSet("test123", e -> baseCacheExecutor.get("test123"));
        /**
         * 通过LcacheCaffeineLocalCache.get操作
         */
        LcacheCaffeineLocalCache.get("test123", e -> baseCacheExecutor.get("test123"));
    }

    @Test
    public void testRedisson() {
        BaseCacheExecutor baseCacheExecutor = CacheClientFactory.getCacheExecutor("test", new LettuceConnectSourceConfig());
        RedissonClient redissonClient = baseCacheExecutor.getRedissonClient();
        //zset批量判断是否存
        String zsetKey = "zset:test:1";
        baseCacheExecutor.zadd(zsetKey, ImmutableMap.of("a", (double) 1, "b", (double) 2, "c", (double) 3), 3600);
        System.out.println(baseCacheExecutor.zscore(zsetKey, "a"));
//        redissonClient.getScoredSortedSet(zsetKey).addAll(ImmutableMap.of("d",(double) 4));
        List<String> strings = Arrays.asList("a", "b", "d");
        RScoredSortedSet<String> scoredSortedSet = redissonClient.getScoredSortedSet(zsetKey, StringCodec.INSTANCE);
        List<Double> score = scoredSortedSet.getScore(strings);
        System.out.println(JSON.toJSONString(score.stream().filter(e -> null != e).map(e -> e.toString()).collect(Collectors.toList())));
    }

    @Test
    public void evalZadd() {
        new Thread(() -> {
            BaseCacheExecutor jedis = CacheClientFactory.getCacheExecutor(CacheConfigModel.jedisPool("test"), new JedisConnectSourceConfig());
            //zset批量判断是否存
            String jzsetKey = "jzset:test:1";
            jedis.del(jzsetKey);
            while (true) {
                try {
                    jedis.zadd(jzsetKey, ImmutableMap.of("a", (double) 1, "b", (double) 2, "c", (double) 3), 3600);
                    jedis.zaddIfKeyExists(jzsetKey, 4, "e", 3600);
                    Map<String, Double> score = jedis.zscoreBatch(jzsetKey, Arrays.asList("a", "b", "c", "e"));
                    System.out.println("jedis : " + JSON.toJSONString(score));
                    Thread.sleep(500L);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        BaseCacheExecutor baseCacheExecutor = CacheClientFactory.getCacheExecutor("test", new LettuceConnectSourceConfig());
        //zset批量判断是否存
        String zsetKey = "lzset:test:1";
        baseCacheExecutor.del(zsetKey);
        while (true) {
            try {
                baseCacheExecutor.zadd(zsetKey, ImmutableMap.of("a", (double) 1, "b", (double) 2, "c", (double) 3), 3600);
                baseCacheExecutor.zaddIfKeyExists(zsetKey, 4, "e", 3600);
                Map<String, Double> score = baseCacheExecutor.zscoreBatch(zsetKey, Arrays.asList("a", "b", "c", "e"));
                System.out.println("lettuce : " + JSON.toJSONString(score));
                Thread.sleep(500L);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testExpireBatch() {
        BaseCacheExecutor baseCacheExecutor = CacheClientFactory.getCacheExecutor("test", new LettuceConnectSourceConfig());
        baseCacheExecutor.set("a","a",60);
        baseCacheExecutor.set("b","b",60);
        try {
            String s = baseCacheExecutor.async().expireBatch(30, "a", "b").get();
            System.out.println(s);
            Map<String,Boolean> res = JSON.parseObject(s,Map.class);
            System.out.println(res.get("a"));
            System.out.println(baseCacheExecutor.ttl("a"));
            System.out.println(baseCacheExecutor.ttl("b"));
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testZaddLua() {
        BaseCacheExecutor baseCacheExecutor = CacheClientFactory.getCacheExecutor("test", new LettuceConnectSourceConfig());
        String key = "zaddIfKeyExistsTest";
        baseCacheExecutor.del(key);
        System.out.println(baseCacheExecutor.zaddIfKeyExists(key, 1, "a", 3600));
        System.out.println(baseCacheExecutor.zrange(key, 0, -1));
        System.out.println(baseCacheExecutor.zadd(key, 2, "b", 3600));
        System.out.println(baseCacheExecutor.zaddIfKeyExists(key, 3, "c", 3600));
        System.out.println(baseCacheExecutor.zrange(key, 0, -1));
    }

    @Test
    public void bzpopTest() {
        BaseCacheExecutor baseCacheExecutor = CacheClientFactory.getCacheExecutor("test", new LettuceConnectSourceConfig());
        BaseCacheExecutor baseCacheExecutor1 = CacheClientFactory.getCacheExecutor("test1", new LettuceConnectSourceConfig());
        BaseCacheExecutor baseCacheExecutor2 = CacheClientFactory.getCacheExecutor("test2", new LettuceConnectSourceConfig());
        String key = "bzpop:test";
        baseCacheExecutor.del(key);
        new Thread(() -> {
            int i = 0;
            while (true) {
                try {
                    i++;
                    baseCacheExecutor.zadd(key, i, i + "", 3600);
                    Thread.sleep(10L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        new Thread(() -> {
            while (true) {
                try {
                    System.out.println("zpopmax : " + baseCacheExecutor1.zpopmax(key));
                    System.out.println("zpopmax+count : " + baseCacheExecutor1.zpopmax(key, 2));
                    System.out.println("bzpopmax : " + baseCacheExecutor1.bzpopmax(1, key));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        new Thread(() -> {
            while (true) {
                try {
                    System.out.println("zpopmin : " + baseCacheExecutor2.zpopmin(key));
                    System.out.println("zpopmin+count : " + baseCacheExecutor2.zpopmin(key, 2));
                    System.out.println("bzpopmin : " + baseCacheExecutor2.bzpopmin(1, key));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        while (true) {
            try {
                System.out.println("zcard : " + baseCacheExecutor.zcard(key));
                Thread.sleep(50L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void lockNotUnlockTest() {
        BaseCacheExecutor baseCacheExecutor = CacheClientFactory.getCacheExecutor("test", new LettuceConnectSourceConfig());
        String lockName = "unLockTest";
        for (int i = 0; i < 5; i++) {
            int finalI = i;
            new Thread(()->{
                try {
                    RLock lock = null;
                    try {
                        lock = baseCacheExecutor.lock(lockName,-1,TimeUnit.SECONDS);
                        while (true){
                            System.out.println("持有锁" + finalI);
                            try {
                                Thread.sleep(5000L);
                                Thread.currentThread().stop();
                            } catch (InterruptedException e) {
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        baseCacheExecutor.unLock(lock);
                        System.out.println("释放锁" + finalI);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
        while (true){
        }
    }

    @Test
    public void asyncTest() {
        BaseCacheExecutor baseCacheExecutor = CacheClientFactory.getCacheExecutor("test", new LettuceConnectSourceConfig());
        baseCacheExecutor.del("lincr1");
        baseCacheExecutor.async().incr("lincr1", 60).thenRun(() -> baseCacheExecutor.asyncL().incr("lincr1"));
        assertEquals(2, Long.parseLong(baseCacheExecutor.get("lincr1")));
        System.out.println("asyncIncr 通过 !");
        baseCacheExecutor.set("test","aaa",60);
        baseCacheExecutor.asyncL().getset("test", "bbb").thenRun(() -> baseCacheExecutor.asyncL().getset("test", "ccc").thenRun(() -> assertEquals("ccc", baseCacheExecutor.get("test"))));
        System.out.println("asyncTest 通过 !");
    }
}
