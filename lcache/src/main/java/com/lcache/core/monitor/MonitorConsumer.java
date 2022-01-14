package com.lcache.core.monitor;

import com.lcache.exception.CacheExceptionFactory;
import com.lcache.util.async.AsyncExecutorUtils;
import io.netty.util.concurrent.DefaultThreadFactory;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: MonitorConsumer
 * @Description: 监控消费
 * @date 2021/7/13 4:00 PM
 */
public class MonitorConsumer {
    /**
     * 消费者数量
     */
    private static int consumerSize = 0;

    /**
     * 消费线程池
     */
    private static ThreadPoolExecutor consumeThreadPool =
            new ThreadPoolExecutor(1, 5, 0, TimeUnit.SECONDS, new SynchronousQueue<>(), new DefaultThreadFactory("cache-monitor-consume"));

    /**
     * 消费
     */
    public synchronized static void doConsume() {
        //只起一个
        if (consumerSize == 0) {
            //增加消费线程
            consumeThreadPool.execute(new ConsumeRunnable(-1));
            //增加消费队列监控，如果队列长度超出，请检查消费逻辑
            AsyncExecutorUtils.submitScheduledTask(() -> {
                //如果队列长度超出，增加error日志
                if (MonitorFactory.MONITOR_QUEUE.size() > MonitorFactory.QUEUE_MONITOR_SIZE) {
                    CacheExceptionFactory.addWarnLog("monitor 消费阻塞警告！剩余消费数值超过 " + MonitorFactory.QUEUE_MONITOR_SIZE);
                    //增加一个30秒消费线程
                    /*consumeThreadPool.execute(new ConsumeRunnable(30000L));*/
                }
            }, 5, 5, TimeUnit.SECONDS);
            consumerSize++;
        }
    }

    /**
     * 消费任务
     */
    static class ConsumeRunnable implements Runnable {

        private long executeTime = -1;

        public ConsumeRunnable() {
        }

        /**
         * 大于0则有结束时间，单位：毫秒
         */
        public ConsumeRunnable(long executeTime) {
            if (executeTime > 0) {
                this.executeTime = System.currentTimeMillis() + executeTime;
            }
        }

        @Override
        public void run() {
            while (executeTime < 0 || executeTime > System.currentTimeMillis()) {
                //消费监听
                try {
                    MonitorFactory.MonitorData take = MonitorFactory.MONITOR_QUEUE.take();
                    if (null != take) {
                        MonitorFactory.MONITOR_MAP.entrySet().forEach(e -> {
                            try {
                                e.getValue().doMonitor(take);
                            } catch (Exception e1) {
                                CacheExceptionFactory.addWarnLog("MonitorProducer comsume error ! monitorType:{}", e1, e.getKey().name());
                            }
                        });
                    }
                } catch (InterruptedException e) {
                    CacheExceptionFactory.addWarnLog("MonitorProducer comsume error !", e);
                }
            }
        }
    }
}
