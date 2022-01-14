package com.lcache.config.model;

import org.apache.commons.pool2.impl.BaseObjectPoolConfig;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: CommonCacheConfig
 * @Description: 基本配置，从DB中获取
 * @date 2021/1/28 12:03 PM
 */
public class CommonCacheConfig {
    /**
     * I / O线程池大小
     */
    private int ioThreadPoolSize = 20;
    /**
     * 计算线程池大小
     */
    private int computationThreadPoolSize = 20;
    /**
     * 最大空闲链接数
     */
    private int maxIdle = 50;
    /**
     * 最大连接数
     */
    private int maxTotal = 300;
    /**
     * 最小空闲连接数
     */
    private int minIdle = 0;
    /**
     * 错误重试次数，不建议设置
     */
    private int maxAttempts = 0;
    /**
     * 获取连接时的最大等待毫秒数(如果设置为阻塞时BlockWhenExhausted),如果超时就抛异常, 小于零:阻塞不确定的时间,  默认-1
     */
    private long maxWaitMillis = BaseObjectPoolConfig.DEFAULT_MAX_WAIT_MILLIS;
    /**
     * 逐出连接的最小空闲时间 默认1800000毫秒(30分钟)
     */
    private long minEvictableIdleTimeMillis = BaseObjectPoolConfig.DEFAULT_MIN_EVICTABLE_IDLE_TIME_MILLIS;
    /**
     * 每次逐出检查时 逐出的最大数目 如果为负数就是 : 1/abs(n), 默认3
     */
    private int numTestsPerEvictionRun = BaseObjectPoolConfig.DEFAULT_NUM_TESTS_PER_EVICTION_RUN;
    /**
     * 对象空闲多久后逐出, 当空闲时间>该值 且 空闲连接>最大空闲数 时直接逐出,不再根据MinEvictableIdleTimeMillis判断  (默认逐出策略)
     */
    private long softMinEvictableIdleTimeMillis = BaseObjectPoolConfig.DEFAULT_MIN_EVICTABLE_IDLE_TIME_MILLIS;
    /**
     * 在获取连接的时候检查有效性, 默认false
     */
    private boolean testOnBorrow = BaseObjectPoolConfig.DEFAULT_TEST_ON_BORROW;
    /**
     * 在空闲时检查有效性, 默认false
     */
    private boolean testWhileIdle = true;
    /**
     * 逐出扫描的时间间隔(毫秒) 如果为负数,则不运行逐出线程, 默认-1
     */
    private long timeBetweenEvictionRunsMillis = BaseObjectPoolConfig.DEFAULT_TIME_BETWEEN_EVICTION_RUNS_MILLIS;

    public int getMaxIdle() {
        return maxIdle;
    }

    public void setMaxIdle(int maxIdle) {
        this.maxIdle = maxIdle;
    }

    public int getMaxTotal() {
        return maxTotal;
    }

    public void setMaxTotal(int maxTotal) {
        this.maxTotal = maxTotal;
    }

    public int getMinIdle() {
        return minIdle;
    }

    public void setMinIdle(int minIdle) {
        this.minIdle = minIdle;
    }

    public int getMaxAttempts() {
        return maxAttempts;
    }

    public void setMaxAttempts(int maxAttempts) {
        this.maxAttempts = maxAttempts;
    }

    public long getMaxWaitMillis() {
        return maxWaitMillis;
    }

    public void setMaxWaitMillis(long maxWaitMillis) {
        this.maxWaitMillis = maxWaitMillis;
    }

    public long getMinEvictableIdleTimeMillis() {
        return minEvictableIdleTimeMillis;
    }

    public void setMinEvictableIdleTimeMillis(long minEvictableIdleTimeMillis) {
        this.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
    }

    public int getNumTestsPerEvictionRun() {
        return numTestsPerEvictionRun;
    }

    public void setNumTestsPerEvictionRun(int numTestsPerEvictionRun) {
        this.numTestsPerEvictionRun = numTestsPerEvictionRun;
    }

    public long getSoftMinEvictableIdleTimeMillis() {
        return softMinEvictableIdleTimeMillis;
    }

    public void setSoftMinEvictableIdleTimeMillis(long softMinEvictableIdleTimeMillis) {
        this.softMinEvictableIdleTimeMillis = softMinEvictableIdleTimeMillis;
    }

    public boolean isTestOnBorrow() {
        return testOnBorrow;
    }

    public void setTestOnBorrow(boolean testOnBorrow) {
        this.testOnBorrow = testOnBorrow;
    }

    public boolean isTestWhileIdle() {
        return testWhileIdle;
    }

    public void setTestWhileIdle(boolean testWhileIdle) {
        this.testWhileIdle = testWhileIdle;
    }

    public long getTimeBetweenEvictionRunsMillis() {
        return timeBetweenEvictionRunsMillis;
    }

    public void setTimeBetweenEvictionRunsMillis(long timeBetweenEvictionRunsMillis) {
        this.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
    }

    public int getIoThreadPoolSize() {
        return ioThreadPoolSize;
    }

    public void setIoThreadPoolSize(int ioThreadPoolSize) {
        this.ioThreadPoolSize = ioThreadPoolSize;
    }

    public int getComputationThreadPoolSize() {
        return computationThreadPoolSize;
    }

    public void setComputationThreadPoolSize(int computationThreadPoolSize) {
        this.computationThreadPoolSize = computationThreadPoolSize;
    }
}
