package com.redhat.atm.service;

import com.redhat.atm.model.ed254.ArrivalSequence;
import com.redhat.atm.model.Subscription;
import com.redhat.atm.repository.SubscriptionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ArrivalSequenceService {

    private final SubscriptionRepository subscriptionRepository;

    @Autowired
    public ArrivalSequenceService(SubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    public void splitMessageToSubscribers(String jmsCorrelationID, ArrivalSequence arrivalSequence) {
        log.debug("splitMessageToSubscribers called. Message Id: {}, arrivalSequence: {}", jmsCorrelationID, arrivalSequence.getTopic());

        Subscription probe = new Subscription();
        probe.getTopicsOfInterest().add(arrivalSequence.getTopic());
        ExampleMatcher matcher = ExampleMatcher.matching().withIgnoreNullValues();
        Example<Subscription> example = Example.of(probe, matcher);

        List<Subscription> subscriptions = subscriptionRepository.findAll(example, Sort.by(Sort.Order.asc("createdAt")));

        for (Subscription subscription : subscriptions) {
            log.debug("Send message to subscription: {} on Queue: {}", subscription.getSubscriptionId(),subscription.getResponseQueue());
        }

    }

}
