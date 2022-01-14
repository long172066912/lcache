package com.lcache.extend.handle.pipeline;

public class PipelineIncr extends PipelineCmd {
    private String key;
    private long incrCount;

    public PipelineIncr(String key, long incrCount) {
        this.key = key;
        this.incrCount = incrCount;
    }

    @Override
    public CMD getCmd() {
        return CMD.INCR;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public long getIncrCount() {
        return incrCount;
    }

    public void setIncrCount(long incrCount) {
        this.incrCount = incrCount;
    }
}
