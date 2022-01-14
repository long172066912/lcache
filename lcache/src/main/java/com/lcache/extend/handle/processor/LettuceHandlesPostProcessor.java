package com.lcache.extend.handle.processor;

import com.lcache.config.BaseCacheConfig;
import com.lcache.connect.RedisConnectionManager;
import com.lcache.core.constant.ConnectTypeEnum;
import com.lcache.core.constant.HandlePostProcessorTypeEnum;
import com.lcache.core.constant.RedisClientConstants;
import com.lcache.core.model.CacheHandleProcessorModel;
import com.lcache.core.processor.AbstractHandlePostProcessor;
import com.lcache.core.processor.factory.HandlePostFactory;
import com.lcache.exception.CacheExceptionFactory;
import com.lcache.executor.CacheExecutorFactory;
import com.lcache.extend.handle.redis.lettuce.connect.LettuceConnectResource;
import io.lettuce.core.RedisNoScriptException;

import javax.annotation.PostConstruct;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: RedisHandlesPostProcessor
 * @Description: redis命令后置处理器实现类
 * @date 2021/1/18 9:05 PM
 */
public class LettuceHandlesPostProcessor extends AbstractHandlePostProcessor {

    private static final String CONNECTION_CLOSE_ERROR = "Connection is closed";

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
        return RedisClientConstants.LETTUCE;
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
        if (cacheHandleProcessorModel.getE() instanceof RedisNoScriptException) {
            //获取执行器调用脚本缓存操作
            CacheExecutorFactory.getCacheExecutor(null, cacheHandleProcessorModel.getCacheConfigModel()).loadLuaScripts();
        }
        //Lettuce非连接池方式增加连接重置功能
        if (cacheHandleProcessorModel.getCacheConfigModel().getConnectTypeEnum().equals(ConnectTypeEnum.SIMPLE) && cacheHandleProcessorModel.getE().getMessage().contains(CONNECTION_CLOSE_ERROR)) {
            //重置连接
            try {
                synchronized (cacheHandleProcessorModel.getCacheConfigModel()) {
                    //获取配置
                    BaseCacheConfig config = CacheExecutorFactory.getRedisSourceConfig(cacheHandleProcessorModel.getCacheConfigModel());
                    //判断连接是否有效
                    LettuceConnectResource resource = (LettuceConnectResource) RedisConnectionManager.getConnectionResource(cacheHandleProcessorModel.getCacheConfigModel(), config).getResource();
                    if (!resource.getStatefulRedisConnection().isOpen()) {
                        //重置连接
                        RedisConnectionManager.resetConnectionResource(cacheHandleProcessorModel.getCacheConfigModel(), config);
                    }
                }
            } catch (Exception e) {
                CacheExceptionFactory.addErrorLog("LettuceHandlesPostProcessor reset connection fail !", e);
            }
        }
    }
}
