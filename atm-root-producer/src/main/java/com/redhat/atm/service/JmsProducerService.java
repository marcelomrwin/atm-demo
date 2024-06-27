package com.redhat.atm.service;

import com.redhat.atm.model.ed254.ArrivalSequence;
import com.redhat.atm.model.ed254.TopicType;
import jakarta.jms.Session;
import jakarta.jms.TextMessage;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import java.io.StringWriter;
import java.util.UUID;

@Service
@Slf4j
public class JmsProducerService {
    private final JmsTemplate jmsTemplate;
    private final Marshaller marshaller;

    @Autowired
    public JmsProducerService(JmsTemplate jmsTemplate) throws JAXBException {
        this.jmsTemplate = jmsTemplate;
        JAXBContext context = JAXBContext.newInstance(ArrivalSequence.class);
        marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
    }

    public void sendMessage(TopicType destination, ArrivalSequence arrivalSequence) throws JAXBException {
        convertAndSend(destination, arrivalSequence);
    }

    private void convertAndSend(TopicType destination, ArrivalSequence arrivalSequence) throws JAXBException {
        StringWriter sw = new StringWriter();
        marshaller.marshal(arrivalSequence, sw);
        jmsTemplate.send(destination.value(), (Session session) -> {
            TextMessage textMessage = session.createTextMessage(sw.toString());
            textMessage.setJMSCorrelationID(UUID.randomUUID().toString());
            log.info("Configured message wit CorrelationID {}", textMessage.getJMSCorrelationID());
            return textMessage;
        });
    }
}
