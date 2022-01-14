package com.lcache.extend.handle.pipeline;

public class PipelineZcard extends PipelineCmd {
    private String key;

    public PipelineZcard(String key) {
        this.key = key;
    }

    @Override
    public CMD getCmd() {
        return CMD.ZCARD;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
