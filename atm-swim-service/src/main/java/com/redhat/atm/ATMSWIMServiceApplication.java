package com.redhat.atm;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;

@SpringBootApplication
@Slf4j
public class ATMSWIMServiceApplication {

    @Autowired
    private Environment env;

    public static void main(String[] args) {
        SpringApplication.run(ATMSWIMServiceApplication.class, args);
    }

    @EventListener(ContextRefreshedEvent.class)
    public void onApplicationStartup(ContextRefreshedEvent event) {
        log.info("Starting {} on container {}",getClass().getName(),env.getProperty("HOSTNAME","localhost"));
    }

}
