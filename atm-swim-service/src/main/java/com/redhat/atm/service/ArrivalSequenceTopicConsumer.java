package com.redhat.atm.service;

import com.redhat.atm.model.ed254.ArrivalSequence;
import jakarta.jms.TextMessage;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.io.StringReader;

@Component
@Slf4j
public class ArrivalSequenceTopicConsumer {

    @Autowired
    ArrivalSequenceService arrivalSequenceService;

    @JmsListener(destination = "${application.topic.name}", containerFactory = "listenerContainerFactoryTopic")
    public void consumeArrivalSequenceTopic(TextMessage message) {
        try {
            log.info("Consuming arrival sequence message ID {} from {}", message.getJMSCorrelationID(), message.getJMSDestination());

            JAXBContext context = JAXBContext.newInstance(ArrivalSequence.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            //just to guarantee the message's integrity
            ArrivalSequence arrivalSequence = (ArrivalSequence) unmarshaller.unmarshal(new StringReader(message.getText()));
            arrivalSequenceService.splitMessageToSubscribers(message.getJMSCorrelationID(), arrivalSequence);

        } catch (Exception e) {
            log.error("An error occurred while trying to process the message from topic", e);
            throw new RuntimeException(e);
        }
    }
}
