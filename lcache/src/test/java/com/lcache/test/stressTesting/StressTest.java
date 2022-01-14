package com.lcache.test.stressTesting;

import com.lcache.client.CacheClientFactory;
import com.lcache.core.BaseCacheExecutor;
import com.lcache.core.constant.ConnectTypeEnum;
import com.lcache.core.model.CacheConfigModel;
import com.lcache.extend.handle.redis.lettuce.config.LettuceConnectSourceConfig;
import io.lettuce.core.api.StatefulConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class StressTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(StressTest.class);

    private static AtomicLong num = new AtomicLong(0);
    private static AtomicLong errNum = new AtomicLong(0);
    private static AtomicLong time = new AtomicLong(0);
    private static int threadNum = 1;

    private static BaseCacheExecutor baseCacheExecutor;

    private static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(5, 100, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>(10000), new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "Lcache 压测线程" + (threadNum ++));
        }
    });

    static {
        threadPoolExecutor.prestartAllCoreThreads();
    }

    public static void main(String[] args) {
//        LettuceClusterConnectSourceConfig lettuceClusterConnectSourceConfig = new LettuceClusterConnectSourceConfig();
//        lettuceClusterConnectSourceConfig.setNodes(new HashSet<>());
//        lettuceClusterConnectSourceConfig.getNodes().add(new LettuceConnectSourceConfig("127.0.0.1",30001,null,1500));
//        lettuceClusterConnectSourceConfig.getNodes().add(new LettuceConnectSourceConfig("127.0.0.1",30002,null,1500));
//        lettuceClusterConnectSourceConfig.getNodes().add(new LettuceConnectSourceConfig("127.0.0.1",30003,null,1500));
//        lettuceClusterConnectSourceConfig.getNodes().add(new LettuceConnectSourceConfig("127.0.0.1",30004,null,1500));
//        lettuceClusterConnectSourceConfig.getNodes().add(new LettuceConnectSourceConfig("127.0.0.1",30005,null,1500));
//        lettuceClusterConnectSourceConfig.getNodes().add(new LettuceConnectSourceConfig("127.0.0.1",30006,null,1500));
//        baseCacheExecutor = CacheClientFactory.getCacheExecutor(new CacheConfigModel("friend").setConnectTypeEnum(ConnectTypeEnum.CLUSTER), lettuceClusterConnectSourceConfig);
        baseCacheExecutor = CacheClientFactory.getCacheExecutor(CacheConfigModel.newCache("friend"),new LettuceConnectSourceConfig());
        test();
    }

    public static void test() {
        LOGGER.info("Lcache Test lettuce begin !");
        baseCacheExecutor.set("test","123",86400);
        baseCacheExecutor.hset("hset","a","b",60);
        threadPoolExecutor.execute(() -> {
            while (true) {
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                time.addAndGet(1);
                if (time.get() <= 300) {
                    LOGGER.info("LcacheTest:" + num);
                    System.out.println("LcacheTest:" + num);
                } else {
                    LOGGER.info("LcacheTest失败次数：" + errNum);
                    break;
                }
                num.set(0);
            }
        });
        final int num1 = 100;
        final int num2 = 500;
        while (true) {
            if (time.get() > 3000) {
                break;
            }
            for (int i = 0; i < num1; i++) {
                try {
                    threadPoolExecutor.execute(() -> {
                        for (int j = 0; j < num2; j++) {
                            try {
                                baseCacheExecutor.get("test" + j);
                            } catch (Exception e) {
                                /**
                                 * 测试单连接情况下redis断开重连
                                 */
                                if (baseCacheExecutor.getCacheConfigModel().getConnectTypeEnum().equals(ConnectTypeEnum.SIMPLE)){
                                    if(!((StatefulConnection) baseCacheExecutor.getConnectResource()).isOpen()){
                                        baseCacheExecutor.close();
                                    }
                                    baseCacheExecutor.returnConnectResource();
                                }
                                errNum.addAndGet(1);
                            }
                            num.addAndGet(1);
                        }
                    });
                } catch (Exception e) {
                }
            }
        }
        LOGGER.info("LcacheTest end !");
    }
}
