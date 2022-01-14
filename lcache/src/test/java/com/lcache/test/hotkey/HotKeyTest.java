package com.lcache.test.hotkey;

import com.lcache.client.CacheClientFactory;
import com.lcache.core.BaseCacheExecutor;
import com.lcache.extend.handle.redis.lettuce.config.LettuceConnectSourceConfig;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class HotKeyTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(HotKeyTest.class);

    private static AtomicLong num = new AtomicLong(0);
    private static AtomicLong errNum = new AtomicLong(0);
    private static AtomicLong time = new AtomicLong(0);
    private static int threadNum = 1;

    private static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(5, 100, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>(10000), new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "Lcache压测线程" + (threadNum ++));
        }
    });

    static {
        threadPoolExecutor.prestartAllCoreThreads();
    }

    @Test
    public void test() {
        BaseCacheExecutor baseCacheExecutor = CacheClientFactory.getCacheExecutor("friend",new LettuceConnectSourceConfig())
                .openLocalCache()
                .setMonitorHotKeyStatisticCapacity(100);
        LOGGER.info("Lcache Test hotKey test begin !");
        threadPoolExecutor.execute(() -> {
            while (true) {
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                time.addAndGet(1);
                if (time.get() <= 300) {
                    LOGGER.info("Lcache Test:" + num);
                    System.out.println("Lcache Test:" + num);
                } else {
                    LOGGER.info("Lcache Test失败次数：" + errNum);
                    break;
                }
                num.set(0);
            }
        });
        final int num1 = 100;
        final int num2 = 50;
        //数据准备
        new Thread(()->{
            while (true){
                System.out.println("修改数值");
                for (int j = 0; j < num2; j++) {
                    baseCacheExecutor.set("test" + j,j + "",3600);
                }
                try {
                    Thread.sleep(7000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        while (true) {
            //10秒后，降低热key起始值
            if (time.get() == 10) {
//                System.out.println("设置热key起始值为100");
                baseCacheExecutor.setIsOpenHotKeyLocalCache(true);
                baseCacheExecutor.setMonitorHotKeyMinValue(100);
            }
            if (time.get() > 300) {
                break;
            }
            for (int i = 0; i < num1; i++) {
                try {
                    threadPoolExecutor.execute(() -> {
                        for (int j = 0; j < num2; j++) {
                            try {
                                baseCacheExecutor.get("test" + j);
                            } catch (Exception e) {
                                errNum.addAndGet(1);
                            }
                            num.addAndGet(1);
                        }
                    });
                } catch (Exception e) {
                }
            }
        }
        LOGGER.info("Lcache Test end !");
    }
}
