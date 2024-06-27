package com.redhat.atm.controller;

import com.redhat.atm.dto.SubscriptionRequest;
import com.redhat.atm.dto.SubscriptionResponse;
import com.redhat.atm.model.Subscription;
import com.redhat.atm.service.SubscriptionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@Tag(
        name = "SWIM",
        description = "ATM SWIM SUBSCRIPTION API"
)
@RequestMapping({"/api/subscription/v1"})
@Slf4j
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @Autowired
    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @GetMapping
    @RequestMapping("/ping")
    public ResponseEntity<?> ping(){
        log.info("PING SUBSCRIPTION");
        return ResponseEntity.ok("pong");
    }

    @PostMapping(
            produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    @RequestMapping("/user/subscribe")
    public ResponseEntity<?> subscribeToArrivalSequence(@RequestBody SubscriptionRequest request, @AuthenticationPrincipal Jwt jwt) {
        log.info("Subscribe request with body: {}", request);
        try {
            Subscription subscription = subscriptionService.subscribe(jwt.getClaimAsString("preferred_username"), request.topicTypes(), jwt.getClaimAsString("amq-role-name"));
            log.info("Successfully created subscription: {}", subscription.getSubscriptionId());
            return new ResponseEntity(new SubscriptionResponse(subscription.getSubscriptionId(), subscription.getResponseQueue(), subscription.getExpiresAt()), HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Error while subscribing", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
