package com.xxm.dataingestionservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@ComponentScan(basePackages = "com.xxm.dataingestionservice.config")
@EnableSwagger2
@EnableScheduling
public class DataIngestionServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(DataIngestionServiceApplication.class, args);
    }

}
