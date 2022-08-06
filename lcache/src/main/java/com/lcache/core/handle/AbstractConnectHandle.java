package com.lcache.core.handle;


import com.lcache.core.constant.HandlePostProcessorTypeEnum;
import com.lcache.core.converters.PostProcessorConvertersAndExecutor;
import com.lcache.core.model.CacheHandleProcessorModel;
import com.lcache.core.processor.AbstractHandlePostProcessor;
import com.lcache.util.BeanFactory;
import com.lcache.util.CacheFunction;

import java.util.List;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: AbstractCommomHandle
 * @Description: 连接执行器
 * @date 2021/1/18 3:20 PM
 */
public abstract class AbstractConnectHandle implements InterfaceCommomHandle {

    protected PostProcessorConvertersAndExecutor postProcessorConverters = BeanFactory.get(PostProcessorConvertersAndExecutor.class);

    /**
     * 获取客户端类型
     * RedisClientConstants
     *
     * @return
     */
    public abstract int getClientType();

    /**
     * 根据操作类型获取执行链路
     *
     * @return
     */
    public List<AbstractHandlePostProcessor> getHandleLinkList() {
        return postProcessorConverters.getHandlePostProcessors(HandlePostProcessorTypeEnum.CONNECT, this.getClientType());
    }

    /**
     * 执行命令
     *
     * @return
     */
    @Override
    public <T> T execute(CacheFunction<T> function) {
        return postProcessorConverters.executeHandles(this.getHandleLinkList(), new CacheHandleProcessorModel<T>(function));
    }

    @Override
    public <T> T execute(CacheFunction<T> function, String key) {
        return this.execute(function);
    }

    @Override
    public <T> T execute(CacheFunction<T> function, String key, Object[] fields) {
        return this.execute(function);
    }

    @Override
    public <T> T execute(CacheFunction<T> function, String[] keys) {
        return this.execute(function);
    }

    @Override
    public <T> T execute(CacheFunction<T> function, int expireSeconds, String key) {
        return this.execute(function);
    }

    @Override
    public <T> T execute(CacheFunction<T> function, int expireSeconds, String key, Object[] fields) {
        return this.execute(function);
    }

    @Override
    public <T> T execute(CacheFunction<T> function, int expireSeconds, String[] keys) {
        return this.execute(function);
    }
}
