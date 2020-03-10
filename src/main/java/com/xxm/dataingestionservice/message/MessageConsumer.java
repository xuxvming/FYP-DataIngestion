package com.xxm.dataingestionservice.message;

import com.xxm.dataingestionservice.ingestion.CustomFile;
import com.xxm.dataingestionservice.ingestion.FileManagerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gcp.pubsub.support.BasicAcknowledgeablePubsubMessage;
import org.springframework.cloud.gcp.pubsub.support.GcpPubSubHeaders;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;

import java.util.HashMap;
import java.util.List;


public class MessageConsumer implements MessageHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageConsumer.class);

    @Autowired
    FileManagerImpl fileManager;

    @Autowired
    MessageProducer messageProducer;


    @Override
    @ServiceActivator(inputChannel = "myInputChannel")
    public void handleMessage(Message<?> message){
        String payload = new String((byte[]) message.getPayload());
        LOGGER.info("Message arrived! Payload: [{}]" , payload);
        BasicAcknowledgeablePubsubMessage originalMessage =
                message.getHeaders().get(GcpPubSubHeaders.ORIGINAL_MESSAGE, BasicAcknowledgeablePubsubMessage.class);
        assert originalMessage != null;
        originalMessage.ack();
        CustomFile file = new CustomFile(payload);
        List<HashMap<String,String >> fileMap = fileManager.getFileRecordsAsMaps(file);
        messageProducer.sendRecordsAsMessage(fileMap,file.getTimeInterval());
    }
}
