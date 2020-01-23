package com.xxm.dataingestionservice.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/v1")
public interface IngestionManager {

    @RequestMapping(value = "/retrieveData/{symbol}/{function}/{timeInterval}", method = RequestMethod.GET)
    Map<String,Object> retrieveData(@PathVariable("symbol") String symbol, @PathVariable("function") String function, @PathVariable("timeInterval") String timeInterval);

    @RequestMapping(value = "retrievdData/{symbol}/{function}/{timeInterval}/download", method = RequestMethod.GET)
    String retrieveDataAsFile(@PathVariable("symbol") String symbol, @PathVariable("function") String function, @PathVariable("timeInterval") String timeInterval);

}
