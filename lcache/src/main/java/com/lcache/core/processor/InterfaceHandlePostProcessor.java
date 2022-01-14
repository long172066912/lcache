package com.lcache.core.processor;


import com.lcache.core.model.CacheHandleProcessorModel;

import java.util.Set;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: InterfaceHandlePostProcessor
 * @Description: 组件操作类后置处理器接口，定义事件类型
 * @date 2021/1/18 11:29 AM
 */
public interface InterfaceHandlePostProcessor {
    /**
     * 指定命令集合
     *
     * @return
     */
    Set<String> specifiedCommands();

    /**
     * 命令执行前执行
     *
     * @param cacheHandleProcessorModel
     */
    void handleBefore(CacheHandleProcessorModel cacheHandleProcessorModel);

    /**
     * 命令执行成功后执行
     *
     * @param cacheHandleProcessorModel
     */
    void onSuccess(CacheHandleProcessorModel cacheHandleProcessorModel);

    /**
     * 命令执行失败后执行
     *
     * @param cacheHandleProcessorModel
     */
    void onFail(CacheHandleProcessorModel cacheHandleProcessorModel);

    /**
     * 命令执行后执行
     *
     * @param cacheHandleProcessorModel
     */
    void handleAfter(CacheHandleProcessorModel cacheHandleProcessorModel);
}
