package com.lcache.extend.handle.processor;

import com.alibaba.fastjson.JSON;
import com.lcache.core.constant.HandlePostProcessorTypeEnum;
import com.lcache.core.constant.RedisClientConstants;
import com.lcache.core.constant.UseTypeEnum;
import com.lcache.core.model.CacheHandleProcessorModel;
import com.lcache.core.processor.AbstractHandlePostProcessor;
import com.lcache.core.processor.factory.HandlePostFactory;
import com.lcache.exception.CacheExceptionFactory;
import com.lcache.util.CacheFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.Set;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: JedisPubSubPostProcessor
 * @Description: redis命令后置处理器实现类
 * @date 2021/1/18 9:05 PM
 */
public class JedisPubSubPostProcessor extends AbstractHandlePostProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(JedisPubSubPostProcessor.class);

    /**
     * 订阅字符串
     */
    private static final String SUBSCRIBE = "subscribe";
    /**
     * 重试间隔时间，
     */
    private static final Long RETRY_TIME = 1000L;

    private static final Set<String> COMMANDS = new HashSet<>();

    static {
        COMMANDS.add("subscribe");
    }

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
        return RedisClientConstants.JEDIS;
    }

    @Override
    @PostConstruct
    public void registerIntoPostFactory() {
        HandlePostFactory.addBeanPostProcessor(this);
    }

    @Override
    public Set<String> specifiedCommands() {
        return COMMANDS;
    }

    @Override
    public void onFail(CacheHandleProcessorModel cacheHandleProcessorModel) {
        //如果是订阅功能，增加重试
        this.retry(cacheHandleProcessorModel.getFunction(), RETRY_TIME, cacheHandleProcessorModel);
    }

    private void retry(CacheFunction function, Long retryTime, CacheHandleProcessorModel cacheHandleProcessorModel) {
        while (true) {
            //设置为业务，让心跳检测能正常修复连接
            cacheHandleProcessorModel.getCacheConfigModel().setUseType(UseTypeEnum.BUSINESS);
            //设置重试状态
            cacheHandleProcessorModel.getCacheConfigModel().setRetry(true);
            try {
                Thread.sleep(retryTime);
            } catch (InterruptedException e) {
            }
            LOGGER.info("Jedis subscribe retry ! CacheConfigModel:[{}]", JSON.toJSONString(cacheHandleProcessorModel.getCacheConfigModel()));
            try {
                function.apply();
            } catch (Exception e) {
                CacheExceptionFactory.addErrorLog("CacheHandleUtils", "retry", "重试失败，继续重试", e);
            }
        }
    }
}
