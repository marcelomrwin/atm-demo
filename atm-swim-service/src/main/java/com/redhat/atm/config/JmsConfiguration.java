package com.redhat.atm.config;

import com.redhat.atm.model.Constants;
import jakarta.jms.JMSException;
import jakarta.jms.Topic;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.apache.activemq.artemis.jms.client.ActiveMQTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableJms
@Slf4j
public class JmsConfiguration {

    @Value("${jms.receiver.activemq.user}")
    private String RECEIVER_BROKER_USERNAME;

    @Value("${jms.receiver.activemq.password}")
    private String RECEIVER_BROKER_PASSWORD;

    @Value("${jms.receiver.activemq.broker-url}")
    private String RECEIVER_BROKER_URL;

    @Value("${jms.publisher.activemq.user}")
    private String PUBLISHER_BROKER_USERNAME;

    @Value("${jms.publisher.activemq.password}")
    private String PUBLISHER_BROKER_PASSWORD;

    @Value("${jms.publisher.activemq.broker-url}")
    private String PUBLISHER_BROKER_URL;


    @Value("${application.topic.ttl}")
    private Integer ttlMessage;

    @Autowired
    private Environment env;


    @Bean
    @Qualifier("receiverConnectionFactory")
    public ActiveMQConnectionFactory receiverConnectionFactory() throws JMSException {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
        connectionFactory.setBrokerURL(RECEIVER_BROKER_URL);
        connectionFactory.setPassword(RECEIVER_BROKER_USERNAME);
        connectionFactory.setUser(RECEIVER_BROKER_PASSWORD);
        return connectionFactory;
    }

    @Bean
    @Qualifier("publisherConnectionFactory")
    public ActiveMQConnectionFactory publisherConnectionFactory() throws JMSException {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
        connectionFactory.setBrokerURL(PUBLISHER_BROKER_URL);
        connectionFactory.setPassword(PUBLISHER_BROKER_USERNAME);
        connectionFactory.setUser(PUBLISHER_BROKER_PASSWORD);
        return connectionFactory;
    }

    @Bean
    @Qualifier("receiver")
    public JmsListenerContainerFactory<?> listenerContainerFactory(@Qualifier("receiverConnectionFactory") ActiveMQConnectionFactory connectionFactory, DefaultJmsListenerContainerFactoryConfigurer configurer) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConcurrency("3-40");
        configurer.configure(factory, connectionFactory);
        return factory;
    }

    @Bean
    @Qualifier("publisherTopic")
    public JmsListenerContainerFactory<?> listenerContainerFactoryTopic(@Qualifier("publisherConnectionFactory") ActiveMQConnectionFactory connectionFactory, DefaultJmsListenerContainerFactoryConfigurer configurer) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConcurrency("1-1");
        configurer.configure(factory, connectionFactory);
        factory.setPubSubDomain(true);
//        factory.setSubscriptionDurable(true);
//        String hostname = env.getProperty("HOSTNAME", "localhost");
//        factory.setClientId(hostname);
        return factory;
    }

    @Bean
    @Qualifier("publisherQueue")
    public JmsListenerContainerFactory<?> listenerContainerFactoryQueue(@Qualifier("publisherConnectionFactory") ActiveMQConnectionFactory connectionFactory, DefaultJmsListenerContainerFactoryConfigurer configurer) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConcurrency("1-10");
        configurer.configure(factory, connectionFactory);
        return factory;
    }

    @Bean
    public CachingConnectionFactory cachingConnectionFactory() throws JMSException {
        return new CachingConnectionFactory(publisherConnectionFactory());
    }

    @Bean
    @Qualifier("publisherTopic")
    public JmsTemplate jmsTemplateTopic() throws JMSException {
        JmsTemplate template = new JmsTemplate();
        template.setConnectionFactory(cachingConnectionFactory());
        template.setPubSubDomain(true);
        template.setDeliveryPersistent(true);
        template.setTimeToLive(TimeUnit.MINUTES.toMillis(ttlMessage));
        return template;
    }

    @Bean
    @Qualifier("publisherQueue")
    public JmsTemplate jmsTemplateQueue() throws JMSException {
        JmsTemplate template = new JmsTemplate();
        template.setConnectionFactory(cachingConnectionFactory());
        template.setDeliveryPersistent(true);
        template.setTimeToLive(TimeUnit.MINUTES.toMillis(ttlMessage));
        return template;
    }

    @Bean
    public Topic arrivalOrderTopic() {
        return new ActiveMQTopic(Constants.ARRIVAL_ORDER_TOPIC,Constants.ARRIVAL_ORDER_TOPIC);
    }

}
