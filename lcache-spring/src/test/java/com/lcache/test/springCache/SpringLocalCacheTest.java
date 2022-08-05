package com.lcache.test.springCache;

import com.google.common.collect.ImmutableMap;
import com.lcache.spring.LcacheSpringLocalCache;
import com.lcache.test.CacheBaseTest;
import com.lcache.test.springCache.local.LocalCacheEvictTestService;
import com.lcache.test.springCache.local.LocalCacheGetTestService;
import com.lcache.test.springCache.local.LocalCachePutTestService;
import com.lcache.test.springCache.service.model.SpringCacheTestReq;
import com.lcache.test.springCache.service.model.SpringCacheTestRes;
import lombok.Data;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class SpringLocalCacheTest extends CacheBaseTest {

    private LcacheSpringLocalCache localCache = new LcacheSpringLocalCache();

    @Autowired
    private LocalCacheGetTestService service;
    @Autowired
    private LocalCachePutTestService putTestService;
    @Autowired
    private LocalCacheEvictTestService evictTestService;

    @Test
    public void test() {
        String key = "lCacheTest";
        String callAbleKey = "lCacheCallAbleTestKey";
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
        Assert.assertEquals(value, localCache.get("stringValue").get());
        Assert.assertEquals(value, service.getStringValue(SpringCacheTestReq.builder().a("getTest2").b(2).build()));
        //map
        Map<String, Object> mapValue = service.getMapValue(req1);
        Assert.assertEquals(mapValue, localCache.get("mapValue").get());
        Assert.assertEquals(mapValue, service.getMapValue(SpringCacheTestReq.builder().a("getTest3").b(2).build()));
        //object
        SpringCacheTestRes objectValue = service.getObjectValue(req1);
        Assert.assertEquals(objectValue, localCache.get("objectValue").get());
        Assert.assertEquals(objectValue, service.getObjectValue(SpringCacheTestReq.builder().a("getTest4").b(2).build()));
    }

    @Test
    public void putTest() {
        SpringCacheTestReq req1 = SpringCacheTestReq.builder().a("getTest1").b(1).build();
        String value = service.getStringValue(req1);
        Assert.assertEquals(value, localCache.get("stringValue").get());
        SpringCacheTestReq req2 = SpringCacheTestReq.builder().a("getTest2").b(2).build();
        putTestService.setStringValue(req2);
        Assert.assertEquals(service.getStringValue(req1), req2.getA());
    }

    @Test
    public void evictTest() {
        SpringCacheTestReq req1 = SpringCacheTestReq.builder().a("getTest1").b(1).build();
        String value = service.getStringValue(req1);
        Assert.assertEquals(value, localCache.get("stringValue").get());
        evictTestService.str(req1);
        Assert.assertNull(localCache.get("stringValue"));
        assertEquals(service.getStringValue(req1), localCache.get("stringValue").get());
    }


    private void check(String key, String callAbleKey, Object value) {
        System.out.println(value);
        localCache.evict(key);
        localCache.evict(callAbleKey);
        localCache.put(key, value);
        assertEquals(localCache.putIfAbsent(key, value).get(), value);
        assertEquals(localCache.get(key).get(), value);
        assertEquals(localCache.get(key, value.getClass()), value);
        assertEquals(localCache.get(callAbleKey, () -> value), value);
        assertEquals(localCache.get(callAbleKey).get(), value);
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
