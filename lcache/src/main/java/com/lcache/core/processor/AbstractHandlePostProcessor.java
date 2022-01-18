package com.lcache.core.processor;


import com.lcache.core.constant.HandlePostProcessorTypeEnum;
import com.lcache.core.model.CacheHandleProcessorModel;
import com.lcache.core.processor.factory.HandlePostFactory;

import javax.annotation.PostConstruct;
import java.util.Set;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: AbstractHandlePostProcessor
 * @Description: 后置处理抽象类
 * @date 2021/1/18 11:52 AM
 */
public abstract class AbstractHandlePostProcessor implements InterfaceHandlePostProcessor {

    /**
     * 获取优先级Id
     *
     * @return
     */
    public abstract int getOrder();

    /**
     * 获取处理器Id
     *
     * @return
     */
    public abstract int getHandlePostId();

    /**
     * 获取处理器类型
     *
     * @return
     */
    public abstract HandlePostProcessorTypeEnum getHandleType();

    /**
     * 获取客户端类型
     *
     * @return
     */
    public abstract int getClientType();

    /**
     * 如果指定了命令，则使用限定命令
     *
     * @return
     */
    @Override
    public Set<String> specifiedCommands() {
        return null;
    }

    /**
     * 注册实现类到工厂
     */
    @PostConstruct
    public void registerIntoPostFactory(){
        HandlePostFactory.addBeanPostProcessor(this);
    }

    @Override
    public void handleBefore(CacheHandleProcessorModel cacheHandleProcessorModel) {
    }

    @Override
    public void handleAfter(CacheHandleProcessorModel cacheHandleProcessorModel) {
    }

    @Override
    public void onSuccess(CacheHandleProcessorModel cacheHandleProcessorModel) {
    }

    @Override
    public void onFail(CacheHandleProcessorModel cacheHandleProcessorModel) {
    }
}
