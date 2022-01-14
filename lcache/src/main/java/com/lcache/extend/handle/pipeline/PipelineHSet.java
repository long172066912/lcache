package com.lcache.extend.handle.pipeline;

public class PipelineHSet extends PipelineCmd {
    private String key;
    private String field;
    private String value;

    public PipelineHSet(String key, String field, String value) {
        this.key = key;
        this.field = field;
        this.value = value;
    }

    @Override
    public CMD getCmd() {
        return CMD.HSET;
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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
