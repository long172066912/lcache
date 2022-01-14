package com.lcache.extend.handle.pipeline;

public class PipelineZrangeWithScores extends PipelineCmd {
    private String key;
    private long start;
    private long end;

    public PipelineZrangeWithScores(String key, long start, long end) {
        this.key = key;
        this.start = start;
        this.end = end;
    }

    @Override
    public CMD getCmd() {
        return CMD.ZRANGE_WITH_SCORES;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }
}
