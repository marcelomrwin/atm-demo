package com.redhat.atm.test;

import jakarta.jms.*;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
public class ActiveMQRestOperationsTest {
    private static final String QUEUE_NAME = "masales-queue";
    private static final String BROKER_URL = "tcp://localhost:61616";
    private static final String BASE_REST_URL = "http://artemis-swim-wconsj-0-svc-rte-nav-portugal.apps.ocp4.masales.cloud/console/jolokia/exec";
    private static final Logger logger = LoggerFactory.getLogger(ActiveMQRestOperationsTest.class);

    @Test
    @Order(1)
    public void sendMessage() throws IOException, JMSException {
        logger.info("Creating the ConnectionFactory");
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(BROKER_URL);

        logger.info("Creating the connection");
        try (Connection connection = connectionFactory.createConnection("admin", "password")) {
            connection.start();

            logger.info("Creating the session");
            try (Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)) {
                TextMessage textMessage = session.createTextMessage("Hello there! " + LocalDateTime.now());
                logger.info("Sending message");
                session.createProducer(session.createQueue(QUEUE_NAME)).send(textMessage);
            }
        }
    }

    @Test
    @Order(2)
    public void tryToConsumeWithAnUnauthorizedConsumer() throws Exception {
        logger.info("Creating the ConnectionFactory for consumer");
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(BROKER_URL);
        JMSException exception = Assertions.assertThrows(JMSException.class, () -> {
            logger.info("Creating the connection to consume the message");
            try (Connection connection = connectionFactory.createConnection("admin-viewer", "password")) {
                connection.start();
                logger.info("Creating the session to consume the message");
                try (Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)) {
                    session.createConsumer(session.createQueue(QUEUE_NAME)).setMessageListener(message -> {
                        try {
                            logger.info("Received message {}\n{}", message.getJMSMessageID(), ((TextMessage) message).getText());
                        } catch (JMSException e) {
                            logger.error("Fail processing the message", e);
                            throw new RuntimeException(e);
                        }
                    });
                }
            }
        });
        logger.warn("Exception {}", exception.getMessage());
    }

    @Test
    @Order(3)
    public void tryToConsumeWithAnAuthorizedConsumer() throws Exception {
        logger.info("Creating the ConnectionFactory for authorized consumer");
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(BROKER_URL);
        logger.info("Creating the connection to consume the message by an authorized consumer");
        try (Connection connection = connectionFactory.createConnection("marcelo", "password")) {
            connection.start();
            logger.info("Creating the session to consume the message by an authorized consumer");

            try (Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)) {
                MessageConsumer messageConsumer = session.createConsumer(session.createQueue(QUEUE_NAME));
                messageConsumer.setMessageListener(message -> {
                    try {
                        logger.info("The authorized consumer received the message {}\n{}", message.getJMSMessageID(), ((TextMessage) message).getText());
                    } catch (JMSException e) {
                        logger.error("Fail processing the message", e);
                        throw new RuntimeException(e);
                    }
                });

                TimeUnit.SECONDS.sleep(5);
            }

        }
    }
}
