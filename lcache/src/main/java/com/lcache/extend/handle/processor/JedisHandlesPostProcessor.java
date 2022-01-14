package com.lcache.extend.handle.processor;

import com.lcache.core.constant.HandlePostProcessorTypeEnum;
import com.lcache.core.constant.RedisClientConstants;
import com.lcache.core.model.CacheHandleProcessorModel;
import com.lcache.core.processor.AbstractHandlePostProcessor;
import com.lcache.core.processor.factory.HandlePostFactory;
import com.lcache.executor.CacheExecutorFactory;
import redis.clients.jedis.exceptions.JedisNoScriptException;

import javax.annotation.PostConstruct;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: RedisHandlesPostProcessor
 * @Description: redis命令后置处理器实现类
 * @date 2021/1/18 9:05 PM
 */
public class JedisHandlesPostProcessor extends AbstractHandlePostProcessor {

    @Override
    public int getOrder() {
        return -1;
    }

    @Override
    public int getHandlePostId() {
        return -1;
    }

    @Override
    public HandlePostProcessorTypeEnum getHandleType() {
        return HandlePostProcessorTypeEnum.HANDLE;
    }

    @Override
    public int getClientType() {
        return RedisClientConstants.JEDIS;
    }

    @Override
    @PostConstruct
    public void registerIntoPostFactory() {
        HandlePostFactory.addBeanPostProcessor(this);
    }

    @Override
    public void handleBefore(CacheHandleProcessorModel cacheHandleProcessorModel) {
    }

    @Override
    public void onSuccess(CacheHandleProcessorModel cacheHandleProcessorModel) {
    }

    @Override
    public void onFail(CacheHandleProcessorModel cacheHandleProcessorModel) {
        /**
         * 如果是lua脚本异常，重新缓存
         */
        if (cacheHandleProcessorModel.getE() instanceof JedisNoScriptException) {
            //获取执行器调用脚本缓存操作
            CacheExecutorFactory.getCacheExecutor(null, cacheHandleProcessorModel.getCacheConfigModel()).loadLuaScripts();
        }
    }
}
