package com.lcache.extend.handle.pipeline;

public class PipelineHincrBy extends PipelineCmd {
    private String key;
    private String field;
    private long value;

    public PipelineHincrBy(String key, String field, long value) {
        this.key = key;
        this.field = field;
        this.value = value;
    }

    @Override
    public CMD getCmd() {
        return CMD.HINCRBY;
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

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }
}
