package com.xxm.dataingestionservice.controller;

public enum RequestSettings {

    SYMBOL("symbol"),
    FUNCTION("function"),
    TIME_INTERVAL("interval"),
    OUTPUT_SIZE ("outputsize"),
    DATA_TYPE("datatype");

    private final String field;
    RequestSettings(String field) {
        this.field = field;
    }

    public String getField() {
        return field;
    }
}
