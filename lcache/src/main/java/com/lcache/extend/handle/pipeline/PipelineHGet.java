package com.lcache.extend.handle.pipeline;

public class PipelineHGet extends PipelineCmd {
    private String key;
    private String field;

    public PipelineHGet(String key, String field) {
        this.key = key;
        this.field = field;
    }

    @Override
    public CMD getCmd() {
        return CMD.HGET;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }
}
