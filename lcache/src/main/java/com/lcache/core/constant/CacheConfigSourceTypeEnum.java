package com.lcache.core.constant;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: ConnectTypeEnum
 * @Description: 连接资源方式枚举
 * @date 2021/1/20 5:43 PM
 */
public enum CacheConfigSourceTypeEnum {
    /**
     * DB方式
     */
    DB(1),
    /**
     * APOLLO
     */
    APOLLO(2),
    /**
     * 自定义
     */
    CUSTOM(3),
    ;

    CacheConfigSourceTypeEnum(int type) {
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
