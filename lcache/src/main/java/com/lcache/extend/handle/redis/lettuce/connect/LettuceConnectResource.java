package com.lcache.extend.handle.redis.lettuce.connect;

import com.lcache.connect.InterfaceConnectResource;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;
import org.apache.commons.pool2.impl.GenericObjectPool;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: LettuceConnectResource
 * @Description: Lettuce连接资源对象
 * @date 2021/2/1 4:01 PM
 */
public class LettuceConnectResource implements InterfaceConnectResource {

    /**
     * 链接资源
     */
    private StatefulRedisConnection statefulRedisConnection;
    /**
     * 连接池
     */
    private GenericObjectPool genericObjectPool;
    /**
     * 集群方式
     */
    private StatefulRedisClusterConnection statefulRedisClusterConnection;

    public StatefulRedisConnection getStatefulRedisConnection() {
        return statefulRedisConnection;
    }

    public LettuceConnectResource setStatefulRedisConnection(StatefulRedisConnection statefulRedisConnection) {
        this.statefulRedisConnection = statefulRedisConnection;
        return this;
    }

    public GenericObjectPool getGenericObjectPool() {
        return genericObjectPool;
    }

    public LettuceConnectResource setGenericObjectPool(GenericObjectPool genericObjectPool) {
        this.genericObjectPool = genericObjectPool;
        return this;
    }

    public StatefulRedisClusterConnection getStatefulRedisClusterConnection() {
        return statefulRedisClusterConnection;
    }

    public LettuceConnectResource setStatefulRedisClusterConnection(StatefulRedisClusterConnection statefulRedisClusterConnection) {
        this.statefulRedisClusterConnection = statefulRedisClusterConnection;
        return this;
    }

    @Override
    public String toString() {
        return "LettuceConnectResource{" +
                "statefulRedisConnection=" + statefulRedisConnection +
                ", genericObjectPool=" + genericObjectPool +
                ", statefulRedisClusterConnection=" + statefulRedisClusterConnection +
                '}';
    }

    @Override
    public void close() {
        if (null != this.statefulRedisConnection) {
            this.statefulRedisConnection.closeAsync();
        }
        if (null != this.genericObjectPool) {
            this.genericObjectPool.close();
        }
        if (null != this.statefulRedisClusterConnection) {
            this.statefulRedisClusterConnection.closeAsync();
        }
    }
}
