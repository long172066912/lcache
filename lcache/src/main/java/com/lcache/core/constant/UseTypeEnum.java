package com.lcache.core.constant;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: UseTypeEnum
 * @Description: 用途枚举
 * @date 2021/1/20 5:43 PM
 */
public enum UseTypeEnum {
    /**
     * 正常业务
     */
    BUSINESS(1),
    /**
     * 管道
     */
    PIPELINE(2),
    /**
     * 发布订阅
     */
    PUBSUB(3),
    /**
     * 队列
     */
    QUEUE(4),
    ;

    UseTypeEnum(int type) {
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
