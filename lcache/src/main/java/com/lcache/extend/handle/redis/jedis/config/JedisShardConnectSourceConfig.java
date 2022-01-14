package com.lcache.extend.handle.redis.jedis.config;

import com.lcache.config.BaseCacheConfig;
import redis.clients.jedis.JedisShardInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: JedisShardConnectSourceConfig
 * @Description: shard方式连接
 * @date 2021/1/27 5:37 PM
 */
public class JedisShardConnectSourceConfig extends BaseCacheConfig {
    /**
     * 节点信息
     */
    private List<JedisShardInfo> shards;

    @Override
    public List<String> getHosts() {
        return new ArrayList<>(shards.stream().map(e -> e.getHost()).collect(Collectors.toSet()));
    }

    public List<JedisShardInfo> getShards() {
        return shards;
    }

    public JedisShardConnectSourceConfig setShards(List<JedisShardInfo> shards) {
        this.shards = shards;
        return this;
    }
}
