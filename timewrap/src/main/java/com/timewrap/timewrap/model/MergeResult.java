package com.timewrap.timewrap.model;

import java.util.List;
import java.util.Map;

public class MergeResult {

    private Map<String, Object> mergedState;
    private List<MergeConflict> conflicts;

    public MergeResult(Map<String, Object> mergedState, List<MergeConflict> conflicts) {
        this.mergedState = mergedState;
        this.conflicts = conflicts;
    }

    public Map<String, Object> getMergedState() { return mergedState; }
    public List<MergeConflict> getConflicts() { return conflicts; }
}