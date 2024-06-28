package com.redhat.atm.service;

import com.redhat.atm.model.MessageLog;
import com.redhat.atm.model.MessageLogDetail;
import com.redhat.atm.model.ed254.ArrivalSequence;
import com.redhat.atm.model.Subscription;
import com.redhat.atm.model.ed254.TopicType;
import com.redhat.atm.repository.SubscriptionRepository;
import jakarta.jms.Session;
import jakarta.jms.TextMessage;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Sort;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class ArrivalSequenceService {

    private final SubscriptionRepository subscriptionRepository;
    private final JmsTemplate jmsTemplate;
    private final Marshaller marshaller;
    private final MessageLogService messageLogService;

    @Autowired
    public ArrivalSequenceService(SubscriptionRepository subscriptionRepository, @Qualifier("publisherQueue") JmsTemplate jmsTemplate, MessageLogService messageLogService) throws JAXBException {
        this.subscriptionRepository = subscriptionRepository;
        this.jmsTemplate = jmsTemplate;
        this.messageLogService = messageLogService;
        JAXBContext context = JAXBContext.newInstance(ArrivalSequence.class);
        marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
    }

    public void splitMessageToSubscribers(String jmsCorrelationID, ArrivalSequence arrivalSequence) throws JAXBException {
        log.info("splitMessageToSubscribers called. Message Id: {}, arrivalSequence: {}", jmsCorrelationID, arrivalSequence.getTopic());

        Subscription probe = new Subscription();
        probe.setTopicsOfInterest(new ArrayList<>());
        probe.getTopicsOfInterest().add(arrivalSequence.getTopic());
        ExampleMatcher matcher = ExampleMatcher.matching().withIgnoreNullValues();
        Example<Subscription> example = Example.of(probe, matcher);

        List<Subscription> subscriptions = subscriptionRepository.findAll(example, Sort.by(Sort.Order.asc("createdAt")));
        log.info("Total subscribers found: {}", subscriptions.size());
        for (Subscription subscription : subscriptions) {
            log.info("Send message to subscription: {} on Queue: {}", subscription.getSubscriptionId(), subscription.getResponseQueue());

            convertAndSend(subscription.getResponseQueue(), arrivalSequence, jmsCorrelationID);

            MessageLog messageLog = messageLogService.findById(jmsCorrelationID);
            MessageLogDetail messageLogDetail = new MessageLogDetail();
            messageLogDetail.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC));
            messageLogDetail.setDestination(subscription.getResponseQueue());
            messageLogDetail.setPayload("Message " + jmsCorrelationID + " sent to queue " + subscription.getResponseQueue());
            messageLog.getMessageLogDetails().add(messageLogDetail);
            messageLogService.save(messageLog);
        }

    }

    private void convertAndSend(String destination, ArrivalSequence arrivalSequence, String jmsCorrelationID) throws JAXBException {
        StringWriter sw = new StringWriter();
        marshaller.marshal(arrivalSequence, sw);
        jmsTemplate.send(destination, (Session session) -> {
            TextMessage textMessage = session.createTextMessage(sw.toString());
            textMessage.setJMSCorrelationID(jmsCorrelationID);
            textMessage.setJMSExpiration(TimeUnit.MINUTES.toMillis(5));
            log.info("Configured message for CorrelationID {} to expires at {}", textMessage.getJMSCorrelationID(), LocalDateTime.now(ZoneOffset.UTC).plusMinutes(5));
            return textMessage;
        });
    }

}
