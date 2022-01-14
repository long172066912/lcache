package com.lcache.extend.handle.pipeline;

public class PipelineZrevRange extends PipelineCmd {
    private String key;

    private long startIndex;

    private long endIndex;

    public PipelineZrevRange(String key, long startIndex, long endIndex) {
        this.key = key;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }

    @Override
    public CMD getCmd() {
        return CMD.ZREVRANGE;
    }

    public String getKey() {
        return key;
    }

    public PipelineZrevRange setKey(String key) {
        this.key = key;
        return this;
    }

    public long getStartIndex() {
        return startIndex;
    }

    public PipelineZrevRange setStartIndex(long startIndex) {
        this.startIndex = startIndex;
        return this;
    }

    public long getEndIndex() {
        return endIndex;
    }

    public PipelineZrevRange setEndIndex(long endIndex) {
        this.endIndex = endIndex;
        return this;
    }
}
