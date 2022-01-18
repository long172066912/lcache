package com.lcache.core.constant;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: HandlePostProcessorTypeEnum
 * @Description: 后置处理器类型
 * @date 2021/1/18 11:59 AM
 */
public enum HandlePostProcessorTypeEnum {
    /**
     * 连接
     */
    CONNECT(1),
    /**
     * 命令
     */
    HANDLE(2),
    ;

    HandlePostProcessorTypeEnum(int type) {
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
