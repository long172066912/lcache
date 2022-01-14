package com.lcache.extend.handle.redis.jedis.config;

import com.lcache.config.BaseCacheConfig;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Protocol;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: JedisConnectSourceConfig
 * @Description: Jedis连接配置类
 * @date 2021/1/19 4:34 PM
 */
public class JedisClusterConnectSourceConfig extends BaseCacheConfig {
    /**
     * 节点
     */
    private Set<HostAndPort> nodes;
    /**
     * 超时时间
     */
    private int timeout = 2000;
    /**
     * 命令执行最长时间
     */
    private int soTimeout = 2000;

    private int database = Protocol.DEFAULT_DATABASE;

    private String pwd = null;
    /**
     * 错误重试次数，不建议设置
     */
    private int maxAttempts = 0;

    @Override
    public List<String> getHosts() {
        return new ArrayList<>(nodes.stream().map(e -> e.getHost()).collect(Collectors.toSet()));
    }

    public Set<HostAndPort> getNodes() {
        return nodes;
    }

    public JedisClusterConnectSourceConfig setNodes(Set<HostAndPort> nodes) {
        this.nodes = nodes;
        return this;
    }

    public int getTimeout() {
        return timeout;
    }

    public JedisClusterConnectSourceConfig setTimeout(int timeout) {
        this.timeout = timeout;
        return this;
    }

    public int getSoTimeout() {
        return soTimeout;
    }

    public JedisClusterConnectSourceConfig setSoTimeout(int soTimeout) {
        this.soTimeout = soTimeout;
        return this;
    }

    public int getDatabase() {
        return database;
    }

    public JedisClusterConnectSourceConfig setDatabase(int database) {
        this.database = database;
        return this;
    }

    public String getPwd() {
        return pwd;
    }

    public JedisClusterConnectSourceConfig setPwd(String pwd) {
        this.pwd = pwd;
        return this;
    }

    public int getMaxAttempts() {
        return maxAttempts;
    }

    public JedisClusterConnectSourceConfig setMaxAttempts(int maxAttempts) {
        this.maxAttempts = maxAttempts;
        return this;
    }
}
