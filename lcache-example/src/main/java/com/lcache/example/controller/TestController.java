package com.lcache.example.controller;

import com.lcache.core.BaseCacheExecutor;
import com.lcache.spring.annotation.Lcache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: TestController
 * @Description: 测试接口
 * @date 2022/8/15 16:30
 */
@RestController
@RequestMapping("/test")
public class TestController {

    private static Logger LOGGER = LoggerFactory.getLogger(TestController.class);

    @Lcache("test1")
    private BaseCacheExecutor cacheExecutor1;
    @Lcache("test2")
    private BaseCacheExecutor cacheExecutor2;
    @Lcache("test3")
    private BaseCacheExecutor cacheExecutor3;

    @PostMapping("/test")
    public void test() {
        System.out.println(cacheExecutor1.get("aa"));
        System.out.println(cacheExecutor2.get("aa"));
        System.out.println(cacheExecutor3.get("aa"));
    }
}
