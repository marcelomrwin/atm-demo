package com.redhat.atm;

import com.redhat.atm.model.ed254.ArrivalSequence;
import com.redhat.atm.model.ed254.Entry;
import com.redhat.atm.model.ed254.TopicType;
import com.redhat.atm.repository.ArrivalSequenceRepository;
import com.redhat.atm.service.ArrivalSequenceProducer;
import com.redhat.atm.service.JmsProducerService;
import jakarta.xml.bind.JAXBException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Random;

@Slf4j
@Component
public class ArrivalSequenceScheduledJob {

    @Autowired
    JmsProducerService jmsProducerService;

    @Autowired
    ArrivalSequenceProducer arrivalSequenceProducer;

    @Autowired
    ArrivalSequenceRepository arrivalSequenceRepository;

    private final Random random = new Random();

    @Scheduled(fixedRateString = "${application.job.interval}", initialDelay = 5000)
    public void run() {
        log.info("Update Queues ...");
        Arrays.stream(TopicType.values()).forEach(topicType -> {
            runJob(topicType);
        });
    }

    @Async
    protected void runJob(TopicType topicType) {
        log.info("Running job for topic {}", topicType);
        arrivalSequenceRepository.getArrivalSequences().computeIfPresent(topicType, (key, asm) -> {

            ArrivalSequence arrivalSequence = asm.getArrivalSequence();

            if (random.nextBoolean()) {
                log.debug("Rotate Arrival Sequence for topic {}", topicType);
                arrivalSequence = arrivalSequenceProducer.rotateArrivalSequence(topicType);
            }

            try {
                jmsProducerService.sendMessage(topicType, arrivalSequence);
            } catch (JAXBException e) {
                log.error("Error while running job for topic {}", topicType, e);
                throw new RuntimeException(e);
            }

            return asm;
        });

    }

}
