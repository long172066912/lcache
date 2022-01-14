package com.lcache.core.monitor;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: MonitorConfig
 * @Description: 热key配置
 * @date 2021/8/5 1:52 PM
 */
public class MonitorConfig {
    /**
     * 热key统计同步频次，5秒同步一次，单位（毫秒）
     */
    public static long HOT_KEY_COUNT_lINTERVAL_MILLISECONDS = 5000L;
    /**
     * 热key统计容量大小
     */
    public static int HOTE_KEY_STATISTIC_CAPACITY = 50;
    /**
     * 热key统计周期内最小值，即5秒内访问达到10次时计入统计
     */
    public static int HOT_KEY_COUNT_LEAST_VALUE = 10;
    /**
     * 监控队列长度
     */
    public static int MONITOR_QUEUE_SIZE = 100000;
    /**
     * 热key定义最小值
     */
    public static int HOT_KEY_MIN_VALUE = 5000;
}
