package com.lcache.extend.handle.pipeline;

public class PipelineRPush extends PipelineCmd {
    private String key;
    private String[] fields;

    public PipelineRPush(String key, String... fields) {
        this.key = key;
        this.fields = fields;
    }

    @Override
    public CMD getCmd() {
        return CMD.RPUSH;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String[] getFields() {
        return fields;
    }

    public void setFields(String[] fields) {
        this.fields = fields;
    }
}
