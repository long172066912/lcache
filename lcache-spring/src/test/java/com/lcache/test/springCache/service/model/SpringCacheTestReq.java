package com.lcache.test.springCache.service.model;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class SpringCacheTestReq implements Serializable {
    private String a;
    private Integer b;
}
