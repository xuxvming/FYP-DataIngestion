package com.xxm.dataingestionservice.ingestion;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xxm.dataingestionservice.controller.FileManager;
import com.xxm.dataingestionservice.controller.IngestionManager;
import com.xxm.dataingestionservice.controller.RequestSettings;
import com.xxm.dataingestionservice.exception.DataRetrievalException;
import com.xxm.dataingestionservice.message.MessageProducer;
import com.xxm.dataingestionservice.request.RequestMaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class IngestionManagerImpl implements IngestionManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(IngestionManagerImpl.class);

    @Value("${service.api.AlphaVantage}")
    private String url;

    @Value("${service.api.AlphaVantage.key}")
    private String key;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    FileManager fileManager;

    @Autowired
    MessageProducer messageProducer;

    public Map retrieveData(String symbol, String function, String timeInterval){
        RequestMaker requestMaker = new RequestMaker(url,key);
        HashMap<String,String> requestParams = constructParams(symbol,function,timeInterval);
        try {
            Optional<String> res = requestMaker.makeRequest(requestParams,"GET");
            if(res.isPresent()){
                String responseString = res.get();
                Map<String,Map<String, Object>> response = objectMapper.readValue(responseString, Map.class);
                return getTodayDataFromJson(response,timeInterval);
            }else {
                throw new DataRetrievalException("Unable to retrieve data");
            }
        } catch (IOException e) {
            throw new DataRetrievalException("Unable to retrieve data");
        }
    }

    public String retrieveDataAsFile(String symbol, String function, String timeInterval){
        HashMap<String,String> requestParams = constructParams(symbol,function,timeInterval);
        requestParams.put(RequestSettings.DATA_TYPE.getField(),"csv");
        RequestMaker requestMaker = new RequestMaker(url,key);
        Optional<String> res = requestMaker.makeRequest(requestParams,"GET");
        String responseString = "";
        if(res.isPresent()){
            responseString = res.get();
            //responseString = getTodayDataFromCsv(res.get());
            CustomFile file = fileManager.StringToFile(responseString,requestParams);
            messageProducer.sendMessageOnFileReceived(file);
            return responseString;
        }
        return responseString;
    }

    private HashMap<String,String> constructParams(String symbol, String function, String timeInterval){
        HashMap<String,String> requestParams = new HashMap<>();
        requestParams.put(RequestSettings.SYMBOL.getField(),symbol);
        requestParams.put(RequestSettings.FUNCTION.getField(),function);
        requestParams.put(RequestSettings.TIME_INTERVAL.getField(),timeInterval);
        requestParams.put(RequestSettings.OUTPUT_SIZE.getField(),"full");
        return requestParams;
    }

    private String getTodayDataFromCsv(String responseString){
        SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-dd");
        String date = format.format(new Date());
        StringBuilder sb  = new StringBuilder();
        String header = getHeader(responseString);
        sb.append(header);
        for (String s: responseString.split("\n")){
            if(s.startsWith(date)){
                sb.append(s);
            }
        }
        return sb.toString();
    }

    private String getHeader(String responseString){
        String[] arr = responseString.split("\n");
        return arr[0];
    }

    private Map getTodayDataFromJson(Map<String, Map<String, Object>> response,String timeInterval){
        SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-dd");
        String date = format.format(new Date());
        Map<String,Object> timeSeries = response.get("Time Series (" + timeInterval + ")");
        timeSeries.entrySet().removeIf(temp -> !temp.getKey().startsWith(date));
        return response;
    }

}
