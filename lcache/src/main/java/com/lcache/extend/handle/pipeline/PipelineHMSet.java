package com.lcache.extend.handle.pipeline;

import java.util.Map;

public class PipelineHMSet extends PipelineCmd {
    private String key;
    private Map<String, String> values;

    public PipelineHMSet(String key, Map<String, String> values) {
        this.key = key;
        this.values = values;
    }

    @Override
    public CMD getCmd() {
        return CMD.HMSET;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Map<String, String> getValues() {
        return values;
    }

    public void setValues(Map<String, String> values) {
        this.values = values;
    }
}
