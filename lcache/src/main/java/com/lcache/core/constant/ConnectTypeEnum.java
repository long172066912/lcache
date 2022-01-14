package com.lcache.core.constant;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: ConnectTypeEnum
 * @Description: 连接方式枚举
 * @date 2021/1/20 5:43 PM
 */
public enum ConnectTypeEnum {
    /**
     * 普通模式
     */
    SIMPLE(1),
    /**
     * 连接池
     */
    POOL(2),
    /**
     * 集群
     */
    CLUSTER(3),
    /**
     * 分片模式，只支持Jedis
     */
    SHARDED(4),
    /**
     * 集群连接池，只支持Lettuce
     */
    CLUSTER_POOL(5),
    ;

    ConnectTypeEnum(int type) {
        this.type = type;
    }

    /**
     * 操作类型
     */
    private int type;

    public int getType() {
        return type;
    }
}
