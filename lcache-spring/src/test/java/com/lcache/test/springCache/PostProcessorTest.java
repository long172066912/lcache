package com.lcache.test.springCache;

import com.lcache.core.BaseCacheExecutor;
import com.lcache.spring.Lcache;
import com.lcache.test.CacheBaseTest;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PostProcessorTest extends CacheBaseTest {

    @Lcache(cacheType = SPRING_CACHE_TYPE)
    private BaseCacheExecutor cacheExecutor;

    public static Integer CONNECT_BEFOR = 0;

    public static Integer CONNECT_SUCCESS = 0;

    public static Integer CONNECT_AFTER = 0;

    public static Integer HANDLE_BEFOR = 0;

    public static Integer HANDLE_SUCCESS = 0;

    public static Integer HANDLE_AFTER = 0;

    @Test
    public void testPostProcessor() {
        cacheExecutor.del("test");
        assertEquals(CONNECT_BEFOR.intValue(), 1);
        assertEquals(CONNECT_SUCCESS.intValue(), 1);
        assertEquals(CONNECT_AFTER.intValue(), 1);
        assertEquals(HANDLE_BEFOR.intValue(), 1);
        assertEquals(HANDLE_SUCCESS.intValue(), 1);
        assertEquals(HANDLE_AFTER.intValue(), 1);
        cacheExecutor.set("test", "test", 60);

        //未开启本地缓存
        assertEquals("test", cacheExecutor.get("test"));
        assertEquals(CONNECT_BEFOR.intValue(), 1);
        assertEquals(CONNECT_SUCCESS.intValue(), 1);
        assertEquals(CONNECT_AFTER.intValue(), 1);
        assertEquals(HANDLE_BEFOR.intValue(), 3);
        assertEquals(HANDLE_SUCCESS.intValue(), 3);
        assertEquals(HANDLE_AFTER.intValue(), 3);

        //开启本地缓存，第一查询会load，多执行一次get
        cacheExecutor.openLocalCache().addLocalCacheKeys("test");
        assertEquals("test", cacheExecutor.get("test"));
        assertEquals(CONNECT_BEFOR.intValue(), 1);
        assertEquals(CONNECT_SUCCESS.intValue(), 1);
        assertEquals(CONNECT_AFTER.intValue(), 1);
        assertEquals(HANDLE_BEFOR.intValue(), 5);
        assertEquals(HANDLE_SUCCESS.intValue(), 5);
        assertEquals(HANDLE_AFTER.intValue(), 5);
    }
}
