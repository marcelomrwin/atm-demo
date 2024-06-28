package com.redhat.atm.service;

import com.redhat.atm.model.Constants;
import com.redhat.atm.model.MessageLog;
import jakarta.jms.JMSException;
import jakarta.jms.TextMessage;
import jakarta.jms.Topic;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.artemis.jms.client.ActiveMQTextMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.annotation.JmsListeners;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
public class ArrivalSequenceConsumer {
    private final JmsTemplate jmsTemplate;
    private final Topic arrivalSequenceTopic;
    private final MessageLogService messageLogService;

    @Autowired
    public ArrivalSequenceConsumer(@Qualifier("publisherTopic") JmsTemplate jmsTemplate, Topic arrivalSequenceTopic, MessageLogService messageLogService) {
        this.jmsTemplate = jmsTemplate;
        this.arrivalSequenceTopic = arrivalSequenceTopic;
        this.messageLogService = messageLogService;
    }

    @JmsListeners({
            @JmsListener(destination = Constants.ARRIVAL_SEQUENCE_DATA_A, containerFactory = "listenerContainerFactory"),
            @JmsListener(destination = Constants.ARRIVAL_SEQUENCE_DATA_B, containerFactory = "listenerContainerFactory"),
            @JmsListener(destination = Constants.ARRIVAL_SEQUENCE_DATA_C, containerFactory = "listenerContainerFactory"),
            @JmsListener(destination = Constants.ARRIVAL_SEQUENCE_DATA_D, containerFactory = "listenerContainerFactory"),
            @JmsListener(destination = Constants.ARRIVAL_SEQUENCE_DATA_E, containerFactory = "listenerContainerFactory")
    })
    public void consumeArrivalSequence(ActiveMQTextMessage message) {
        try {
            log.debug("Received arrival sequence message from queue: {}\nMessage Id: {}\nCorrelation Id: {}\n{} ", message.getJMSDestination(), message.getJMSMessageID(), message.getJMSCorrelationID(), message.getText());

            // outbox table pattern
            MessageLog messageLog = new MessageLog();
            messageLog.setMessageId(message.getJMSCorrelationID());
            messageLog.setPayload(message.getText());
            messageLog.setCreatedAt(LocalDateTime.now(ZoneId.of(ZoneOffset.UTC.getId())));
            messageLogService.save(messageLog);
            log.info("Calling redirectToTopic");
            redirectToTopic(message);
        } catch (JMSException e) {
            log.error("An error occurred while trying to process the message", e);
            throw new RuntimeException(e);
        }
    }


    @Async
    protected CompletableFuture<Void> redirectToTopic(ActiveMQTextMessage message) {
        try {
            log.info("Sending message {} to topic {}", message.getJMSCorrelationID(), arrivalSequenceTopic.getTopicName());
            jmsTemplate.send(arrivalSequenceTopic, (session) -> {
                TextMessage textMessage = session.createTextMessage(message.getText());
                textMessage.setJMSCorrelationID(message.getJMSCorrelationID());
                return textMessage;
            });
        } catch (JMSException e) {
            log.error("An error occurred while trying to process the message", e);
            throw new RuntimeException(e);
        }
        return CompletableFuture.completedFuture(null);
    }
}
