package com.lcache.extend.handle.pipeline;

public class PipelineGet extends PipelineCmd {
    private String key;

    public PipelineGet(String key) {
        this.key = key;
    }

    @Override
    public CMD getCmd() {
        return CMD.GET;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
