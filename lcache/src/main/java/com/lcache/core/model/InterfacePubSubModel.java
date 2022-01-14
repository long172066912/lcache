package com.lcache.core.model;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: InterfacePubSubModel
 * @Description: 发布订阅model封装
 * @date 2021/2/26 4:44 PM
 */
@FunctionalInterface
public interface InterfacePubSubModel {
    /**
     * 对message的处理，业务自己实现
     *
     * @param message
     */
    public void onMessage(String message);
}
