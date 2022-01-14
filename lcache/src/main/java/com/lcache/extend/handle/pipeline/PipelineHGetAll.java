package com.lcache.extend.handle.pipeline;

public class PipelineHGetAll extends PipelineCmd {
    private String key;

    public PipelineHGetAll(String key) {
        this.key = key;
    }

    @Override
    public CMD getCmd() {
        return CMD.HGETALL;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

}
