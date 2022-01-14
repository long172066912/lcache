package com.lcache.extend.handle.redis.lettuce.config;


import com.lcache.config.BaseCacheConfig;

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
public class LettuceClusterConnectSourceConfig extends BaseCacheConfig {
    /**
     * 节点与配置
     */
    private Set<LettuceConnectSourceConfig> nodes;
    /**
     * 超时配置
     */
    private int soTimeout = 2000;
    /**
     * 是否自动重连
     * true
     */
    private boolean autoReconnect = true;
    /**
     * 是否验证集群节点成员身份
     * 默认true，这里设置为false不验证
     */
    private boolean validateClusterNodeMembership = false;
    /**
     * 集群重定向的限制次数
     * 默认5，不建议设置
     */
    private int maxRedirects = 0;

    @Override
    public List<String> getHosts() {
        return new ArrayList<>(nodes.stream().map(e -> e.getHost()).collect(Collectors.toSet()));
    }

    public boolean isAutoReconnect() {
        return autoReconnect;
    }

    public LettuceClusterConnectSourceConfig setAutoReconnect(boolean autoReconnect) {
        this.autoReconnect = autoReconnect;
        return this;
    }

    public boolean isValidateClusterNodeMembership() {
        return validateClusterNodeMembership;
    }

    public LettuceClusterConnectSourceConfig setValidateClusterNodeMembership(boolean validateClusterNodeMembership) {
        this.validateClusterNodeMembership = validateClusterNodeMembership;
        return this;
    }

    public int getMaxRedirects() {
        return maxRedirects;
    }

    public LettuceClusterConnectSourceConfig setMaxRedirects(int maxRedirects) {
        this.maxRedirects = maxRedirects;
        return this;
    }

    public Set<LettuceConnectSourceConfig> getNodes() {
        return nodes;
    }

    public LettuceClusterConnectSourceConfig setNodes(Set<LettuceConnectSourceConfig> nodes) {
        this.nodes = nodes;
        return this;
    }

    public int getSoTimeout() {
        return soTimeout;
    }

    public LettuceClusterConnectSourceConfig setSoTimeout(int soTimeout) {
        this.soTimeout = soTimeout;
        return this;
    }
}
