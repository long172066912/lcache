package com.lcache.test.springCache;

import com.google.common.collect.ImmutableMap;
import com.lcache.core.BaseCacheExecutor;
import com.lcache.spring.Lcache;
import com.lcache.spring.LcacheRedissonCache;
import com.lcache.test.CacheBaseTest;
import com.lcache.test.springCache.service.SpringCacheEvictTestService;
import com.lcache.test.springCache.service.SpringCacheGetTestService;
import com.lcache.test.springCache.service.SpringCachePutTestService;
import com.lcache.test.springCache.service.model.SpringCacheTestReq;
import com.lcache.test.springCache.service.model.SpringCacheTestRes;
import lombok.Data;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class SpringCacheTest extends CacheBaseTest {

    @Lcache(cacheType = SPRING_CACHE_TYPE)
    private BaseCacheExecutor cacheExecutor;
    private LcacheRedissonCache lcache;

    @Before
    public void init(){
        this.lcache = new LcacheRedissonCache(SPRING_CACHE_TYPE, cacheExecutor);
    }

    @Autowired
    private SpringCacheGetTestService service;
    @Autowired
    private SpringCachePutTestService putTestService;
    @Autowired
    private SpringCacheEvictTestService evictTestService;

    @Test
    public void test() {
        String key = "lcacheTest";
        String callAbleKey = "lacheCallAbleTestKey";
        check(key, callAbleKey, "test");
        Map<String, String> map = ImmutableMap.of("a", "b", "c", "d");
        check(key, callAbleKey, map);
        check(key, callAbleKey, new TestData(1, "测试", 5));
        System.out.println("测试通过");
    }

    @Test
    public void getTest() {
        //string
        SpringCacheTestReq req1 = SpringCacheTestReq.builder().a("getTest1").b(1).build();
        String value = service.getStringValue(req1);
        assertEquals(value, lcache.get("stringValue").get());
        assertEquals(value, service.getStringValue(SpringCacheTestReq.builder().a("getTest2").b(2).build()));
        //map
        Map<String, Object> mapValue = service.getMapValue(req1);
        assertEquals(mapValue, lcache.get("mapValue").get());
        assertEquals(mapValue, service.getMapValue(SpringCacheTestReq.builder().a("getTest3").b(2).build()));
        //object
        SpringCacheTestRes objectValue = service.getObjectValue(req1);
        assertEquals(objectValue, lcache.get("objectValue").get());
        assertEquals(objectValue, service.getObjectValue(SpringCacheTestReq.builder().a("getTest4").b(2).build()));
    }

    @Test
    public void putTest() {
        SpringCacheTestReq req1 = SpringCacheTestReq.builder().a("getTest1").b(1).build();
        String value = service.getStringValue(req1);
        assertEquals(value, lcache.get("stringValue").get());
        SpringCacheTestReq req2 = SpringCacheTestReq.builder().a("getTest2").b(2).build();
        putTestService.setStringValue(req2);
        assertEquals(service.getStringValue(req1), req2.getA());
    }

    @Test
    public void evictTest() {
        SpringCacheTestReq req1 = SpringCacheTestReq.builder().a("getTest1").b(1).build();
        String value = service.getStringValue(req1);
        assertEquals(value, lcache.get("stringValue").get());
        evictTestService.str(req1);
        Assert.assertNull(lcache.get("stringValue"));
        assertEquals(service.getStringValue(req1), lcache.get("stringValue").get());
    }


    private void check(String key, String callAbleKey, Object value) {
        lcache.evict(key);
        lcache.evict(callAbleKey);
        lcache.put(key, value);
        assertEquals(lcache.putIfAbsent(key, value).get(), value);
        assertEquals(lcache.get(key).get(), value);
        assertEquals(lcache.get(key, value.getClass()), value);
        assertEquals(lcache.get(callAbleKey, () -> value), value);
        assertEquals(lcache.get(callAbleKey).get(), value);
    }

    @Data
    private static class TestData implements Serializable {

        public TestData(Integer age, String name, int level) {
            this.age = age;
            this.name = name;
            this.level = level;
        }

        private Integer age;
        private String name;
        private int level;
    }
}
