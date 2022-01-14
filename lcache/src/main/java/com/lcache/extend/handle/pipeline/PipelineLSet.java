package com.lcache.extend.handle.pipeline;

public class PipelineLSet extends PipelineCmd {
    private String key;
    private long index;
    private String value;

    public PipelineLSet(String key, long index, String value) {
        this.key = key;
        this.index = index;
        this.value = value;
    }

    @Override
    public CMD getCmd() {
        return CMD.LSET;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public long getIndex() {
        return index;
    }

    public void setIndex(long index) {
        this.index = index;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
