package com.lcache.test.springCache.local;

import com.lcache.spring.LcacheLocalCacheEvict;
import com.lcache.spring.LcacheManager;
import com.lcache.test.springCache.service.model.SpringCacheTestReq;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

@Service
public class LocalCacheEvictTestService {

    @LcacheLocalCacheEvict(key = "'stringValue'")
    public void str(SpringCacheTestReq req){
    }

    @CacheEvict(cacheNames = LcacheManager.LCACHE_SPRING_LOCAL_CACHE, key = "'mapValue'")
    public void map(SpringCacheTestReq req){
    }

    @LcacheLocalCacheEvict(key = "'objectValue'")
    public void object(SpringCacheTestReq req){
    }
}
