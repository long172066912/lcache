package com.lcache.core.monitor;


import com.lcache.core.BaseCacheExecutor;
import com.lcache.core.cache.localcache.LocalCacheSubscriber;
import com.lcache.core.cache.localcache.RedisLocalCacheFactory;
import com.lcache.core.constant.MonitorTypeEnum;
import com.lcache.exception.CacheExceptionFactory;
import com.lcache.extend.handle.monitor.CacheCounterMonitorConsumer;
import com.lcache.extend.handle.monitor.CacheHotKeyMonitorConsumer;
import com.lcache.extend.handle.monitor.CacheTimerMonitorConsumer;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: MonitorFactory
 * @Description: 监控数据工厂
 * @date 2021/7/5 3:21 PM
 */
public class MonitorFactory {
    /**
     * 是否开启热key本地缓存，需要主动设置
     */
    private static Boolean isOpenHotKeyLocalCache = false;


    /**
     * Monitor实现类集合，一种类型支持一个
     */
    protected static Map<MonitorTypeEnum, AbstractCacheMonitorConsumer> MONITOR_MAP = new ConcurrentHashMap<>();

    /**
     * 监控命令队列，长度为int最大值
     */
    protected static LinkedBlockingQueue<MonitorData> MONITOR_QUEUE = new LinkedBlockingQueue(MonitorConfig.MONITOR_QUEUE_SIZE);

    protected static final int QUEUE_MONITOR_SIZE = 10000;

    static {
        //非spring项目需要主动注入2个监控器
        MONITOR_MAP.put(MonitorTypeEnum.TIMER, new CacheTimerMonitorConsumer());
        MONITOR_MAP.put(MonitorTypeEnum.COUNT, new CacheCounterMonitorConsumer());
        MONITOR_MAP.put(MonitorTypeEnum.HOTKEY, new CacheHotKeyMonitorConsumer());
    }

    /**
     * 注册消费者实现类
     *
     * @param type
     * @param monitorConsumer
     */
    public static void monitorRegist(MonitorTypeEnum type, AbstractCacheMonitorConsumer monitorConsumer) {
        MONITOR_MAP.put(type, monitorConsumer);
    }

    public static Boolean isOpenHotKeyLocalCache() {
        return isOpenHotKeyLocalCache;
    }

    public static void setIsOpenHotKeyLocalCache(BaseCacheExecutor executor, Boolean isOpen) {
        isOpenHotKeyLocalCache = isOpen;
        //如果开启热key本地缓存，启动监听
        if (isOpenHotKeyLocalCache) {
            LocalCacheSubscriber.addSubscriber(executor);
        }
    }

    /**
     * 提交监控需要处理的热key
     *
     * @param key
     */
    public static void submitHotKey(String key) {
        if (!isOpenHotKeyLocalCache) {
            return;
        }
        RedisLocalCacheFactory.addTransienceLocalCacheKey(key, true);
    }

    /**
     * 队列中添加消息
     *
     * @param monitorData
     */
    public static void addMonitorMsg(MonitorData monitorData) {
        try {
            MONITOR_QUEUE.offer(monitorData);
        } catch (Exception e) {
            CacheExceptionFactory.addWarnLog("MonitorProducer addCommands error ! monitorData:{}", e, monitorData.toString());
        }
    }

    /**
     * @author JerryLong
     * @version V1.0
     * @Title: MonitorFactory
     * @Description: 监控实体信息
     * @date 2021/7/5 3:56 PM
     */
    public static class MonitorData {
        private String cacheType;
        private String commands;
        private String key;
        private List<String> keys;
        private int executeTime;
        private boolean result;

        public static MonitorData builder() {
            return new MonitorData();
        }

        public MonitorData build() {
            return this;
        }

        public String getCacheType() {
            return cacheType;
        }

        public MonitorData cacheType(String cacheType) {
            this.cacheType = cacheType;
            return this;
        }

        public String getCommands() {
            return commands;
        }

        public MonitorData commands(String commands) {
            this.commands = commands;
            return this;
        }

        public String getKey() {
            return key;
        }

        public MonitorData key(String key) {
            this.key = key;
            return this;
        }

        public List<String> getKeys() {
            return keys;
        }

        public MonitorData keys(List<String> keys) {
            this.keys = keys;
            return this;
        }

        public boolean getResult() {
            return result;
        }

        public MonitorData result(boolean result) {
            this.result = result;
            return this;
        }

        public int getExecuteTime() {
            return executeTime;
        }

        public MonitorData executeTime(int executeTimes) {
            this.executeTime = executeTimes;
            return this;
        }

        @Override
        public String toString() {
            return "MonitorData{" +
                    ", cacheType='" + cacheType + '\'' +
                    ", commands='" + commands + '\'' +
                    ", key='" + key + '\'' +
                    ", keys=" + keys +
                    ", executeTime=" + executeTime +
                    ", result='" + result + '\'' +
                    '}';
        }
    }
}
