package com.xxm.dataingestionservice.utils;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;

public class RequestMaker {

    private final String url;
    private final String apiKey;
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestMaker.class);

    public RequestMaker(String url, String apiKey) {
        this.url = url;
        this.apiKey = apiKey;
    }

    public Optional<String> makeRequest(Map<String, String> params, String method) {
        HttpRequestBase requestBase = getRequest(method, url, params);
        String res = null;
        try (CloseableHttpClient closeableHttpClient = HttpClients.createDefault()) {
            CloseableHttpResponse response = closeableHttpClient.execute(requestBase);
            LOGGER.info("Sending [{}] request to [{}]", method, url);
            HttpEntity entity = response.getEntity();
            res = EntityUtils.toString(entity);
        } catch (IOException e) {
            LOGGER.error("Error Making request !",e);
        }
        return Optional.ofNullable(res);
    }


    private String buildStringParams(Map<String, String> params) {
        StringBuilder result = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            result.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
            result.append("&");
        }
        String resultString = result.toString();
        return resultString.length() > 0
                ? resultString.substring(0, resultString.length() - 1)
                : resultString;
    }

    private HttpRequestBase getRequest(String method, String url, Map<String, String> params) {
        if (method.equals("GET")) {
            String stringParams;
            stringParams = buildStringParams(params);
            String executeURI = url + "?" + stringParams + "&apikey=" + apiKey;
            return new HttpGet(executeURI);
        } else if (method.equals("POST")) {
            return new HttpPost(url);
        }
        return null;
    }

}
