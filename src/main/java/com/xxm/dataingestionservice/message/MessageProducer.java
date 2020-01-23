package com.xxm.dataingestionservice.message;

import com.xxm.dataingestionservice.controller.FileManager;
import com.xxm.dataingestionservice.ingestion.CustomFile;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.Map;

public class MessageProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageProducer.class);

    @Autowired
    private KafkaTemplate<String,Object> kafkaTemplate;


    @Autowired
    FileManager fileManager;

    public void sendMessageOnFileReceived(CustomFile file){
       ProducerRecord<String,Object> record = new ProducerRecord<>("file.received", file);
       ListenableFuture<SendResult<String,Object>> future = kafkaTemplate.send(record);
       future.addCallback(new ListenableFutureCallback<>() {
           @Override
           public void onFailure(Throwable throwable) {
                LOGGER.error("Unable to send message: [{}]", throwable.getMessage());
           }

           @Override
           public void onSuccess(SendResult<String, Object> stringObjectSendResult) {
               LOGGER.info("Sent message [{}]",file);
           }
       });
    }

    public void sendMessage(Map<String,String> message){
        ProducerRecord<String, Object> record = new ProducerRecord<>("file.simulate",message);
        ListenableFuture<SendResult<String,Object>> future = kafkaTemplate.send(record);
        future.addCallback(new ListenableFutureCallback<>() {
            @Override
            public void onSuccess(SendResult<String, Object> stringHashMapSendResult) {
                LOGGER.info("Sent message [{}]",message);
            }
            @Override
            public void onFailure(Throwable throwable) {
                LOGGER.error("Unable to send message: [{}]", throwable.getMessage());
            }

        });
    }




}
