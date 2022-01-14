package com.lcache.core.constant;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: ConnectTypeEnum
 * @Description: 命令数据类型枚举
 * @date 2021/1/20 5:43 PM
 */
public enum CommandsDataTypeEnum {
    /**
     * 未知，需要设置
     */
    UNKNOWN,
    /**
     * 字符串
     */
    STRING,
    /**
     * list
     */
    LIST,
    /**
     * set
     */
    SET,
    /**
     * zset
     */
    ZSET,
    /**
     * hash
     */
    HASH,
    /**
     * geo
     */
    GEO,
    /**
     * bitmap
     */
    BITMAP,
    /**
     * lock
     */
    LOCK,
    /**
     * HYPERLOGLOG
     */
    HYPERLOGLOG,
    /**
     * pubsub
     */
    PUBSUB,
    /**
     * eval
     */
    EVAL,
    /**
     * echo
     */
    ECHO,
    /**
     * SLOWLOG
     */
    SLOWLOG,
    /**
     * EXPIRE
     */
    EXPIRE,
    /**
     * 自定义的
     */
    OTHER;
}
