package com.xxm.dataingestionservice.controller;

import com.xxm.dataingestionservice.ingestion.CustomFile;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1")
public interface FileManager {

    @RequestMapping(value = "/file/{symbol}/{date}",method = RequestMethod.GET)
    Map getFileInformation(@PathVariable("symbol") String symbol, @PathVariable("date") String date);

    @RequestMapping(value ="file/{symbol}",method = RequestMethod.GET)
    Map<String, List<HashMap<String, String>>> getFileInformation(@PathVariable("symbol") String symbol);

    CustomFile StringToFile(String content, Map fileParams);

    List<HashMap<String, String>> getFileRecordsAsMaps(CustomFile file);
}
