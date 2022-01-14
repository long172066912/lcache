package com.lcache.test;

import org.junit.jupiter.api.BeforeAll;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CacheTestApplication.class)
public class CacheBaseTest {

    public static final String SPRING_CACHE_TYPE = "test";

    @BeforeAll
    public void init() {

    }
}
