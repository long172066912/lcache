package com.lcache.extend.handle.pipeline;

public class PipelinePTtl extends PipelineCmd {
    private String key;

    public PipelinePTtl(String key) {
        this.key = key;
    }

    @Override
    public CMD getCmd() {
        return CMD.PTTL;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
