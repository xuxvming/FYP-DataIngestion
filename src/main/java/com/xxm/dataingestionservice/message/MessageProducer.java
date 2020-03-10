package com.xxm.dataingestionservice.message;

import com.xxm.dataingestionservice.controller.FileManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gcp.pubsub.core.PubSubTemplate;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageProducer{

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageProducer.class);

    @Autowired
    PubSubTemplate template;

    @Autowired
    FileManager fileManager;

    @Value("${service.message.topic.received}")
    private String fileReceivedTopic;

    @Value("${service.message.topic.simulate}")
    private String fileSimulateTopic;

    public void sendMessageOnFileReceived(File file){
        ListenableFuture<String> future = template.publish(fileReceivedTopic,file.toString());
        future.addCallback(new ListenableFutureCallback<>() {
            @Override
            public void onFailure(Throwable throwable) {
                LOGGER.error("Unable to send message: [{}]", throwable.getMessage());
            }

            @Override
            public void onSuccess(String message) {
                LOGGER.info("Sent message [{}]", message);
            }
        });
    }

    public void sendMessage(Map<String,String> message){
        ListenableFuture<String> future = template.publish(fileSimulateTopic,message.toString());

        future.addCallback(new ListenableFutureCallback<>() {
            @Override
            public void onFailure(Throwable throwable) {
                LOGGER.error("Unable to send message: [{}]", throwable.getMessage());
            }

            @Override
            public void onSuccess(String msg) {
                LOGGER.info("Sent message [{}]", message.toString());
            }
        });

    }

    public void sendRecordsAsMessage(List<HashMap<String, String>> fileRecords,String timeInterval){
        Runnable runnable = () -> {
            int counter = 0;
            while (counter<fileRecords.size()){
                HashMap<String, String> records = fileRecords.get(counter);
                sendMessage(records);
                try {
                    LOGGER.info("The next message will be sent after [{}] milliseconds",getSleepTime(timeInterval));
                    Thread.sleep(getSleepTime(timeInterval));
                } catch (InterruptedException e) {
                    LOGGER.error("Thread interrupted");
                }
                counter++;
            }
        };
        runnable.run();
    }

    private long getSleepTime(String timeInterval){
        Pattern pattern = Pattern.compile("(\\d+)(.*)");
        long sleepTime = 0;
        Matcher matcher = pattern.matcher(timeInterval);
        if (matcher.matches()){
            if (matcher.group(2).equals("min")) {
                sleepTime = Long.parseLong(matcher.group(1)) * 60000;
            }
        }
        return sleepTime;
    }

}
