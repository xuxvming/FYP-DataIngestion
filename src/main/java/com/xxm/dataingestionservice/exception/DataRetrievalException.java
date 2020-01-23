package com.xxm.dataingestionservice.exception;

public class DataRetrievalException extends RuntimeException {
    private final String message;
    public DataRetrievalException(String message){
        this.message = message;
    }
}


