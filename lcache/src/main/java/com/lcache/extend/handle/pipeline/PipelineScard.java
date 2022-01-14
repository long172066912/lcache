package com.lcache.extend.handle.pipeline;

public class PipelineScard extends PipelineCmd {
    private String key;

    public PipelineScard(String key) {
        this.key = key;
    }

    @Override
    public CMD getCmd() {
        return CMD.SCARD;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
