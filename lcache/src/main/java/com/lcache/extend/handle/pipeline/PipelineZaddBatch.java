package com.lcache.extend.handle.pipeline;

import java.util.Map;

public class PipelineZaddBatch extends PipelineCmd {
    private String key;
    private Map<String, Double> scoreMembers;

    @Override
    public CMD getCmd() {
        return CMD.ZADDBATCH;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Map<String, Double> getScoreMembers() {
        return scoreMembers;
    }

    public void setScoreMembers(Map<String, Double> scoreMembers) {
        this.scoreMembers = scoreMembers;
    }

    public PipelineZaddBatch(String key, Map<String, Double> scoreMembers) {
        this.key = key;
        this.scoreMembers = scoreMembers;
    }
}
