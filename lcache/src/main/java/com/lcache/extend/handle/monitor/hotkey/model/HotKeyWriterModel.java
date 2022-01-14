package com.lcache.extend.handle.monitor.hotkey.model;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: HotKeyWriterModel
 * @Description: 热key写入实例，写到文件中，一行一个（转为json）
 * @date 2021/7/6 5:51 PM
 */
public class HotKeyWriterModel {

    public HotKeyWriterModel() {
    }

    public HotKeyWriterModel(String host, String appName, String cacheType, String dataStructure, String key, int count, String time) {
        this.host = host;
        this.appName = appName;
        this.cacheType = cacheType;
        this.dataStructure = dataStructure;
        this.key = key;
        this.count = count;
        this.time = time;
    }

    /**
     * 连接实例id列表
     */
    private String host;
    /**
     * 应用名称
     */
    private String appName;
    /**
     * redis连接类型
     */
    private String cacheType;
    /**
     * 数据结构
     */
    private String dataStructure;
    /**
     * key名称
     */
    private String key;
    /**
     * 统计次数
     */
    private int count;
    /**
     * 记录时间
     */
    private String time;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getCacheType() {
        return cacheType;
    }

    public void setCacheType(String cacheType) {
        this.cacheType = cacheType;
    }

    public String getDataStructure() {
        return dataStructure;
    }

    public void setDataStructure(String dataStructure) {
        this.dataStructure = dataStructure;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "HotKeyWriterModel{" +
                "host='" + host + '\'' +
                ", appName='" + appName + '\'' +
                ", cacheType='" + cacheType + '\'' +
                ", dataStructure='" + dataStructure + '\'' +
                ", key='" + key + '\'' +
                ", count=" + count +
                ", time='" + time + '\'' +
                '}';
    }
}
