package com.lcache.extend.handle.redis.lettuce.config;

import com.lcache.config.BaseCacheConfig;
import redis.clients.jedis.Protocol;

import java.util.Arrays;
import java.util.List;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: JedisConnectSourceConfig
 * @Description: Jedis连接配置类
 * @date 2021/1/19 4:34 PM
 */
public class LettuceConnectSourceConfig extends BaseCacheConfig {

    public LettuceConnectSourceConfig() {
    }

    public LettuceConnectSourceConfig(String host, int port, String pwd, int timeout) {
        this.host = host;
        this.port = port;
        this.pwd = pwd;
        this.timeout = timeout;
    }

    private int database = Protocol.DEFAULT_DATABASE;

    private String host = "127.0.0.1";

    private int port = 6379;

    private String pwd = null;
    /**
     * 连接超时
     */
    private int timeout = 2000;
    /**
     * 连接激活之前是否执行PING命令，不建议使用
     * false
     */
    private boolean pingBeforeActivateConnection = false;
    /**
     * 是否自动重连
     * true
     */
    private boolean autoReconnect = true;
    /**
     * 请求队列容量
     * 默认 2147483647(Integer#MAX_VALUE)
     */
    private int requestQueueSize = Integer.MAX_VALUE;
    /**
     * 超时配置
     */
    private int soTimeout = 1500;

    @Override
    public List<String> getHosts() {
        return Arrays.asList(host);
    }

    public int getDatabase() {
        return database;
    }

    public void setDatabase(int database) {
        this.database = database;
    }

    public String getHost() {
        return host;
    }

    public LettuceConnectSourceConfig setHost(String host) {
        this.host = host;
        return this;
    }

    public int getPort() {
        return port;
    }

    public LettuceConnectSourceConfig setPort(int port) {
        this.port = port;
        return this;
    }

    public String getPwd() {
        return pwd;
    }

    public LettuceConnectSourceConfig setPwd(String pwd) {
        this.pwd = pwd;
        return this;
    }

    public int getTimeout() {
        return timeout;
    }

    public LettuceConnectSourceConfig setTimeout(int timeout) {
        this.timeout = timeout;
        return this;
    }

    public boolean isPingBeforeActivateConnection() {
        return pingBeforeActivateConnection;
    }

    public LettuceConnectSourceConfig setPingBeforeActivateConnection(boolean pingBeforeActivateConnection) {
        this.pingBeforeActivateConnection = pingBeforeActivateConnection;
        return this;
    }

    public boolean isAutoReconnect() {
        return autoReconnect;
    }

    public LettuceConnectSourceConfig setAutoReconnect(boolean autoReconnect) {
        this.autoReconnect = autoReconnect;
        return this;
    }

    public int getRequestQueueSize() {
        return requestQueueSize;
    }

    public LettuceConnectSourceConfig setRequestQueueSize(int requestQueueSize) {
        this.requestQueueSize = requestQueueSize;
        return this;
    }

    public int getSoTimeout() {
        return soTimeout;
    }

    public LettuceConnectSourceConfig setSoTimeout(int soTimeout) {
        this.soTimeout = soTimeout;
        return this;
    }
}
