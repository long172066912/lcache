package com.lcache.extend.handle.pipeline;

public class PipelineZremRangeByRank extends PipelineCmd {
    private String key;
    private long start;
    private long end;

    public PipelineZremRangeByRank(String key, long start, long end) {
        this.key = key;
        this.start = start;
        this.end = end;
    }

    @Override
    public CMD getCmd() {
        return CMD.ZREM_RANGE_BY_RANK;
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

    public PipelineZremRangeByRank setStart(long start) {
        this.start = start;
        return this;
    }

    public long getEnd() {
        return end;
    }

    public PipelineZremRangeByRank setEnd(long end) {
        this.end = end;
        return this;
    }
}
