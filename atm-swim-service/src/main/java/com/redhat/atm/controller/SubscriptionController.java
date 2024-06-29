package com.redhat.atm.controller;

import com.redhat.atm.dto.SubscriptionRequest;
import com.redhat.atm.dto.SubscriptionResponse;
import com.redhat.atm.dto.TopicResponse;
import com.redhat.atm.dto.UnSubscriptionRequest;
import com.redhat.atm.model.Subscription;
import com.redhat.atm.model.ed254.TopicType;
import com.redhat.atm.service.ArtemisQueueManagementService;
import com.redhat.atm.service.SubscriptionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Tag(
        name = "SWIM",
        description = "ATM SWIM SUBSCRIPTION API"
)
@RequestMapping({"/api/subscription/v1"})
@Slf4j
public class SubscriptionController {

    private final SubscriptionService subscriptionService;
    private final ArtemisQueueManagementService artemisQueueManagementService;
    @Value("${jms.publisher.activemq.broker-url}")
    private String PUBLISHER_BROKER_URL;

    @Autowired
    public SubscriptionController(SubscriptionService subscriptionService, ArtemisQueueManagementService artemisQueueManagementService) {
        this.subscriptionService = subscriptionService;
        this.artemisQueueManagementService = artemisQueueManagementService;
    }

    @RequestMapping(method = RequestMethod.GET, path = "/ping", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<?> ping() {
        log.info("PING SUBSCRIPTION");
        return ResponseEntity.ok("pong");
    }

    @RequestMapping(method = RequestMethod.POST, path = "/user/subscribe", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> subscribeToArrivalSequence(@RequestBody SubscriptionRequest request, @AuthenticationPrincipal Jwt jwt) {
        log.info("Subscribe request with body: {}", request);
        try {
            Subscription subscription = subscriptionService.subscribe(jwt.getClaimAsString("preferred_username"), request.topicTypes(), jwt.getClaimAsString("amq-role-name"));
            log.info("Successfully created subscription: {}", subscription.getSubscriptionId());
            return new ResponseEntity<>(new SubscriptionResponse(subscription.getSubscriptionId(), PUBLISHER_BROKER_URL, 61616, subscription.getResponseQueue(), subscription.getExpiresAt()), HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Error while subscribing", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @RequestMapping(method = RequestMethod.POST, path = "/user/unsubscribe", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> unSubscribeToArrivalSequence(@RequestBody UnSubscriptionRequest request, @AuthenticationPrincipal Jwt jwt) {
        log.info("UnSubscribe request with body: {}", request);
        try {
            Subscription subscription = subscriptionService.findById(request.subscriptionId());
            if (subscription == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            // This logic can be placed on the service
            if (!subscription.getSubscriber().equals(jwt.getClaimAsString("preferred_username")))
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

            subscriptionService.unsubscribe(subscription);
            artemisQueueManagementService.syncrhonizeQueues();
            log.info("Successfully remove the subscription: {}", subscription.getSubscriptionId());
            //can return anything
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error while subscribing", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @RequestMapping(method = RequestMethod.GET, path = "/admin/subscriptions", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> getSubscriptions(@AuthenticationPrincipal Jwt jwt) {
        log.info("Get subscriptions");

        List<Subscription> allSubscriptions = subscriptionService.getAllSubscriptions();
        if (allSubscriptions.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(allSubscriptions);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/user/topics")
    public ResponseEntity<List<TopicResponse>> listTopics() {
        TopicType[] topicTypes = TopicType.values();

        List<TopicResponse> responses = Arrays.stream(topicTypes).map(t -> {
            return new TopicResponse(t.value(), t.value());
        }).toList();

        return ResponseEntity.ok(responses);
    }
}
