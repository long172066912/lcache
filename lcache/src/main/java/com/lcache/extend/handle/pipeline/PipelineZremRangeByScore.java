package com.lcache.extend.handle.pipeline;

public class PipelineZremRangeByScore extends PipelineCmd {
    private String key;
    private double min;
    private double max;

    public PipelineZremRangeByScore(String key, double min, double max) {
        this.key = key;
        this.min = min;
        this.max = max;
    }

    @Override
    public CMD getCmd() {
        return CMD.ZREM_RANGE_BY_SCORE;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public double getMin() {
        return min;
    }

    public PipelineZremRangeByScore setMin(double min) {
        this.min = min;
        return this;
    }

    public double getMax() {
        return max;
    }

    public PipelineZremRangeByScore setMax(double max) {
        this.max = max;
        return this;
    }
}
