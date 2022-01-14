package com.lcache.extend.handle.pipeline;

public class PipelineDel extends PipelineCmd {
    private String[] keys;

    public PipelineDel(String... keys) {
        this.keys = keys;
    }

    @Override
    public CMD getCmd() {
        return CMD.DEL;
    }

    public String[] getKeys() {
        return keys;
    }

    public void setKeys(String[] keys) {
        this.keys = keys;
    }
}
