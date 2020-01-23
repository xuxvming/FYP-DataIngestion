package com.xxm.dataingestionservice.config;

import com.xxm.dataingestionservice.controller.FileManager;
import com.xxm.dataingestionservice.controller.IngestionManager;
import com.xxm.dataingestionservice.ingestion.FileManagerImpl;
import com.xxm.dataingestionservice.ingestion.IngestionManagerImpl;
import com.xxm.dataingestionservice.message.MessageConsumer;
import com.xxm.dataingestionservice.message.MessageProducer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceConfig {

    @Bean
    public IngestionManager ingestionManager(){
        return new IngestionManagerImpl();
    }

    @Bean
    public FileManager fileManager(){ return new FileManagerImpl(); }

    @Bean
    public MessageProducer messageProducer(){return new MessageProducer();}

    @Bean
    public MessageConsumer messageConsumer(){return new MessageConsumer();}

}
