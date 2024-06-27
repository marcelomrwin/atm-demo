package com.redhat.atm.service;

import com.redhat.atm.model.Subscription;
import com.redhat.atm.model.ed254.TopicType;
import com.redhat.atm.repository.SubscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

@Service
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final MessageService messageService;

    @Autowired
    public SubscriptionService(SubscriptionRepository subscriptionRepository, MessageService messageService) {
        this.subscriptionRepository = subscriptionRepository;
        this.messageService = messageService;
    }

    @Transactional
    public Subscription subscribe(String subscriber, List<TopicType> topicTypes) throws Exception {
        Subscription subscription = new Subscription();
        subscription.setSubscriber(subscriber);
        UUID uuid = UUID.randomUUID();
        subscription.setSubscriptionId(uuid);
        subscription.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC));
        subscription.setExpiresAt(subscription.getCreatedAt().plusDays(1));
        subscription.setTopicsOfInterest(topicTypes);
        subscription.setResponseQueue(uuid.toString());

        Subscription sub = subscriptionRepository.save(subscription);
        messageService.updateSubscriptions();

        return sub;
    }
}
