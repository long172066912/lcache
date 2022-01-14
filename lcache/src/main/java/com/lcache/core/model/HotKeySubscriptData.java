package com.lcache.core.model;


import com.lcache.core.constant.CacheCommonConstants;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: HotKeySubscriptData
 * @Description: //TODO (用一句话描述该文件做什么)
 * @date 2021/8/19 6:49 PM
 */
public class HotKeySubscriptData {

    public HotKeySubscriptData() {
        this.podName = CacheCommonConstants.IP;
    }

    public HotKeySubscriptData(String key, Integer type) {
        this.podName = CacheCommonConstants.IP;
        this.key = key;
        this.type = type;
    }

    /**
     * 节点名称，存ip
     */
    private String podName;
    /**
     * key
     */
    private String key;
    /**
     * 操作类型
     */
    private Integer type;

    public String getPodName() {
        return podName;
    }

    public void setPodName(String podName) {
        this.podName = podName;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public boolean isLocalHost() {
        return this.podName.equals(CacheCommonConstants.IP) ? true : false;
    }

    public boolean isNewKey() {
        return this.type == 1 ? true : false;
    }
}
