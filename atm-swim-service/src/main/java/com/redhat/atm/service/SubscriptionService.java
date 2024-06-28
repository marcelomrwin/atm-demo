package com.redhat.atm.service;

import com.redhat.atm.model.Subscription;
import com.redhat.atm.model.ed254.TopicType;
import com.redhat.atm.repository.SubscriptionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final MessageService messageService;

    @Autowired
    public SubscriptionService(SubscriptionRepository subscriptionRepository, MessageService messageService) {
        this.subscriptionRepository = subscriptionRepository;
        this.messageService = messageService;
    }

    @Transactional(readOnly = true)
    public Subscription findById(String uuid) {
        UUID id = null;
        try {
            id = UUID.fromString(uuid);
        } catch (Exception e) {
            return null;
        }
        return subscriptionRepository.findById(id).orElse(null);
    }

    @Transactional
    public Subscription subscribe(String subscriber, List<TopicType> topicTypes, String roleName) throws Exception {
        Subscription subscription = new Subscription();
        subscription.setSubscriber(subscriber);
        UUID uuid = UUID.randomUUID();
        subscription.setSubscriptionId(uuid);
        subscription.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC));
        subscription.setExpiresAt(subscription.getCreatedAt().plusDays(1));
        subscription.setTopicsOfInterest(topicTypes);
        subscription.setResponseQueue(uuid.toString());
        subscription.setSubscriberRoleName(roleName);

        Subscription sub = subscriptionRepository.save(subscription);
        messageService.updateSubscriptions();

        return sub;
    }

    @Transactional(readOnly = true)
    public List<Subscription> getAllSubscriptions() {
        return subscriptionRepository.findAll();
    }

    @Transactional
    public void unsubscribe(Subscription subscription) throws Exception {
        subscriptionRepository.delete(subscription);
        messageService.updateSubscriptions();
    }
}
