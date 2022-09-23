package com.lcache.test.springCache.local;

import com.google.common.collect.ImmutableMap;
import com.lcache.spring.manager.LcacheLocalCacheAble;
import com.lcache.spring.manager.LcacheManager;
import com.lcache.test.springCache.service.model.SpringCacheTestReq;
import com.lcache.test.springCache.service.model.SpringCacheTestRes;
import com.lcache.test.util.CommonUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class LocalCacheGetTestService {

    @LcacheLocalCacheAble(key = "'stringValue'")
    public String getStringValue(SpringCacheTestReq req){
        return req.getA() + req.getB();
    }

    @LcacheLocalCacheAble(key = "'mapValue'")
    public Map<String,Object> getMapValue(SpringCacheTestReq req){
        return ImmutableMap.of("k1",req.getA(),"k2",req.getB());
    }

    @Cacheable(cacheNames = LcacheManager.LCACHE_SPRING_LOCAL_CACHE, key = "'objectValue'")
    public SpringCacheTestRes getObjectValue(SpringCacheTestReq req){
        return SpringCacheTestRes.builder().d(req.getB()).c(req.getA()).data(CommonUtils.bean2Map(req)).build();
    }
}
