package com.lcache.test.springCache.processor;

import com.lcache.core.constant.HandlePostProcessorTypeEnum;
import com.lcache.core.constant.RedisClientConstants;
import com.lcache.core.model.CacheHandleProcessorModel;
import com.lcache.core.processor.AbstractHandlePostProcessor;
import com.lcache.test.springCache.PostProcessorTest;
import org.springframework.stereotype.Component;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: RedisHandlesPostProcessor
 * @Description: redis命令后置处理器实现类
 * @date 2021/1/18 9:05 PM
 */
@Component
public class TestLettuceConnectPostProcessor extends AbstractHandlePostProcessor {

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
        return HandlePostProcessorTypeEnum.CONNECT;
    }

    @Override
    public int getClientType() {
        return RedisClientConstants.LETTUCE;
    }

    @Override
    public void handleBefore(CacheHandleProcessorModel cacheHandleProcessorModel) {
        PostProcessorTest.CONNECT_BEFOR ++;
    }

    @Override
    public void onSuccess(CacheHandleProcessorModel cacheHandleProcessorModel) {
        PostProcessorTest.CONNECT_SUCCESS ++;
    }

    @Override
    public void handleAfter(CacheHandleProcessorModel cacheHandleProcessorModel) {
        PostProcessorTest.CONNECT_AFTER ++;
    }

    @Override
    public void onFail(CacheHandleProcessorModel cacheHandleProcessorModel) {

    }
}
