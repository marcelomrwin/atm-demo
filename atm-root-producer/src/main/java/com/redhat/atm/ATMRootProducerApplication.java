package com.redhat.atm;

import com.redhat.atm.model.ed254.ArrivalSequence;
import com.redhat.atm.model.ed254.TopicType;
import com.redhat.atm.service.ArrivalSequenceProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@EnableScheduling
@SpringBootApplication
@EnableAsync
@Slf4j
public class ATMRootProducerApplication {

    @Autowired
    private Environment env;

    @Autowired
    ArrivalSequenceProducer arrivalSequenceProducer;

    public static void main(String[] args) {
        SpringApplication.run(ATMRootProducerApplication.class, args);
    }

    @EventListener(ContextRefreshedEvent.class)
    public void onApplicationStartup(ContextRefreshedEvent event) {
        log.info("Starting up...Loading fake data");
        log.info("Generating the initial lists for each topic");
        final List<CompletableFuture<ArrivalSequence>> futures = new ArrayList<>();
        Arrays.stream(TopicType.values()).forEach(topicType -> {
            log.info("Generating the initial lists for topic {}", topicType);
            futures.add(arrivalSequenceProducer.generateArrivalSequenceAsync(topicType));
        });
        futures.forEach(future -> {
            future.thenAccept(result -> {
                log.info("Finished generating the initial lists for topic {}", result.getTopic());
            }).join();
        });
    }

}
