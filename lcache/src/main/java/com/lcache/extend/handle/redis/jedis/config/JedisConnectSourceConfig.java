package com.lcache.extend.handle.redis.jedis.config;

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
public class JedisConnectSourceConfig extends BaseCacheConfig {
    /**
     * 超时时间
     */
    private int timeout = 2000;
    /**
     * 命令执行最长时间
     */
    private int soTimeout = 2000;

    private int database = Protocol.DEFAULT_DATABASE;
    /**
     * cluster不用传
     */
    private String host = "127.0.0.1";
    /**
     * cluster不用传
     */
    private int port = 6379;

    private String pwd = null;

    @Override
    public List<String> getHosts() {
        return Arrays.asList(host);
    }

    public int getTimeout() {
        return timeout;
    }

    public JedisConnectSourceConfig setTimeout(int timeout) {
        this.timeout = timeout;
        return this;
    }

    public int getSoTimeout() {
        return soTimeout;
    }

    public JedisConnectSourceConfig setSoTimeout(int soTimeout) {
        this.soTimeout = soTimeout;
        return this;
    }

    public int getDatabase() {
        return database;
    }

    public JedisConnectSourceConfig setDatabase(int database) {
        this.database = database;
        return this;
    }

    public String getHost() {
        return host;
    }

    public JedisConnectSourceConfig setHost(String host) {
        this.host = host;
        return this;
    }

    public int getPort() {
        return port;
    }

    public JedisConnectSourceConfig setPort(int port) {
        this.port = port;
        return this;
    }

    public String getPwd() {
        return pwd;
    }

    public JedisConnectSourceConfig setPwd(String pwd) {
        this.pwd = pwd;
        return this;
    }
}
