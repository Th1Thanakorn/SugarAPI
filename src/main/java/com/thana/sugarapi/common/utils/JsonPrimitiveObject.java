package com.thana.sugarapi.common.utils;

public class JsonPrimitiveObject {

    private final boolean isPrimitive;
    private final String type;
    private final Number value;
    private final Number min;
    private final Number max;

    public JsonPrimitiveObject(boolean isPrimitive, String type, Number value, Number min, Number max) {
        this.isPrimitive = isPrimitive;
        this.type = type;
        this.value = value;
        this.min = min;
        this.max = max;
    }

    public boolean isPrimitive() {
        return isPrimitive;
    }

    public String getType() {
        return type;
    }

    public Number getValue() {
        return value;
    }

    public Number getMin() {
        return min;
    }

    public Number getMax() {
        return max;
    }
}
