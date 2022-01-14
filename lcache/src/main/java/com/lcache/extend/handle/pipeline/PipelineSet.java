package com.lcache.extend.handle.pipeline;

public class PipelineSet extends PipelineCmd {
    private String key;
    private String value;
    private int time = -1;

    public PipelineSet(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public PipelineSet(String key, String value, int time) {
        this.key = key;
        this.value = value;
        this.time = time;
    }

    @Override
    public CMD getCmd() {
        return CMD.SET;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }
}
