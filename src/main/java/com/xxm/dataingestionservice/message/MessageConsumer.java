package com.xxm.dataingestionservice.message;

import com.xxm.dataingestionservice.controller.FileManager;
import com.xxm.dataingestionservice.ingestion.CustomFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@EnableKafka
public class MessageConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageConsumer.class);

    @Autowired
    MessageProducer messageProducer;

    @Autowired
    FileManager fileManager;

    private final Pattern pattern = Pattern.compile("(\\d+)(.*)");

    @KafkaListener(topics = "file.received",groupId = "random-consumer")
    public void consume(CustomFile file){
        LOGGER.info("Received: [{}]",file.getLocation());
        List<HashMap<String, String>> fileRecords = fileManager.getFileRecordsAsMaps(file);
        sendRecordsAsMessage(fileRecords,getSleepTime(file.getTimeInterval()));
    }

    private void sendRecordsAsMessage(List<HashMap<String, String>> fileRecords,long sleepTime){
        Runnable runnable = () -> {
            int counter = 0;
            while (counter<fileRecords.size()){
                HashMap<String, String> records = fileRecords.get(counter);
                messageProducer.sendMessage(records);
                try {
                    LOGGER.info("The next message will be sent after [{}] milliseconds",sleepTime);
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                   LOGGER.error("Thread interrupted");
                }
                counter++;
            }
        };
        runnable.run();
    }

    private long getSleepTime(String timeInterval){
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
