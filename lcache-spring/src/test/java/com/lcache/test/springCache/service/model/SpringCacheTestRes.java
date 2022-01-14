package com.lcache.test.springCache.service.model;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
@Builder
public class SpringCacheTestRes implements Serializable {
    private String c;
    private Integer d;
    private Map<String, Object> data;
}
