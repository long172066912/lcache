package com.lcache.test.springCache.processor;

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
import com.lcache.test.springCache.PostProcessorTest;
import io.lettuce.core.RedisNoScriptException;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: RedisHandlesPostProcessor
 * @Description: redis命令后置处理器实现类
 * @date 2021/1/18 9:05 PM
 */
@Component
public class TestLettuceHandlesPostProcessor extends AbstractHandlePostProcessor {

    @Override
    public int getOrder() {
        return 1;
    }

    @Override
    public int getHandlePostId() {
        return 1;
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
    public void handleBefore(CacheHandleProcessorModel cacheHandleProcessorModel) {
        PostProcessorTest.HANDLE_BEFOR ++;
    }

    @Override
    public void onSuccess(CacheHandleProcessorModel cacheHandleProcessorModel) {
        PostProcessorTest.HANDLE_SUCCESS ++;
    }

    @Override
    public void handleAfter(CacheHandleProcessorModel cacheHandleProcessorModel) {
        PostProcessorTest.HANDLE_AFTER ++;
    }

    @Override
    public void onFail(CacheHandleProcessorModel cacheHandleProcessorModel) {

    }
}
