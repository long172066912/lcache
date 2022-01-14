package com.lcache.test.springCache.service;

import com.google.common.collect.ImmutableMap;
import com.lcache.test.CacheBaseTest;
import com.lcache.test.springCache.service.model.SpringCacheTestReq;
import com.lcache.test.springCache.service.model.SpringCacheTestRes;
import com.lcache.test.util.CommonUtils;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class SpringCachePutTestService {

    @CachePut(cacheNames = CacheBaseTest.SPRING_CACHE_TYPE, key = "'stringValue'")
    public String setStringValue(SpringCacheTestReq req){
        return req.getA();
    }

    @CachePut(cacheNames = CacheBaseTest.SPRING_CACHE_TYPE, key = "'mapValue'")
    public Map<String,Object> setMapValue(SpringCacheTestReq req){
        return ImmutableMap.of("k1",req.getA(),"k2",req.getB());
    }

    @CachePut(cacheNames = CacheBaseTest.SPRING_CACHE_TYPE, key = "'objectValue'")
    public SpringCacheTestRes setObjectValue(SpringCacheTestReq req){
        return SpringCacheTestRes.builder().d(req.getB()).c(req.getA()).data(CommonUtils.bean2Map(req)).build();
    }
}
