package com.lcache.test.springCache.service;

import com.lcache.test.CacheBaseTest;
import com.lcache.test.springCache.service.model.SpringCacheTestReq;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

@Service
public class SpringCacheEvictTestService {

    @CacheEvict(cacheNames = CacheBaseTest.SPRING_CACHE_TYPE, key = "'stringValue'")
    public void str(SpringCacheTestReq req){
    }

    @CacheEvict(cacheNames = CacheBaseTest.SPRING_CACHE_TYPE, key = "'mapValue'")
    public void map(SpringCacheTestReq req){
    }

    @CacheEvict(cacheNames = CacheBaseTest.SPRING_CACHE_TYPE, key = "'objectValue'")
    public void object(SpringCacheTestReq req){
    }
}
