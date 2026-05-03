package com.timewrap.timewrap.model;

public class MergeConflict {

     private String field;
    private Object sourceValue;
    private Object targetValue;

    public MergeConflict(String field, Object sourceValue, Object targetValue) {
        this.field = field;
        this.sourceValue = sourceValue;
        this.targetValue = targetValue;
    }

    public String getField() { return field; }
    public Object getSourceValue() { return sourceValue; }
    public Object getTargetValue() { return targetValue; }
}
