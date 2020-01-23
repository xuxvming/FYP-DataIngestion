package com.xxm.dataingestionservice.ingestion;

import java.io.File;
import java.io.Serializable;

public class CustomFile extends File implements Serializable {

    private String location;
    private String timeInterval;
    private String function;
    private String timeStamp;

    public CustomFile(){
        super("");
    }

    public CustomFile(String location, String timeInterval, String function, String timeStamp) {
        super(location);
        this.location = location;
        this.function = function;
        this.timeInterval = timeInterval;
        this.timeStamp = timeStamp;
    }

    public String getLocation() {
        return location;
    }

    public String getTimeInterval() {
        return timeInterval;
    }

    public String getFunction() {
        return function;
    }

    public String getTimeStamp() {
        return timeStamp;
    }
}
