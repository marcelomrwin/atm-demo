package com.redhat.atm.controller;

import com.redhat.atm.dto.SubscriptionRequest;
import com.redhat.atm.dto.SubscriptionResponse;
import com.redhat.atm.model.Subscription;
import com.redhat.atm.service.SubscriptionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@Tag(
        name = "SWIM",
        description = "ATM SWIM SUBSCRIPTION API"
)
@RequestMapping({"/subscription/v1"})
@Slf4j
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @Autowired
    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @PostMapping(
            produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    public ResponseEntity<?> subscribeToArrivalSequence(@RequestBody @Valid SubscriptionRequest request, BindingResult error) {
        log.debug("Creation request with body: {}", request);
        try {
            Subscription subscription = subscriptionService.subscribe(null, null);
            log.info("Successfully created subscription: {}", subscription.getSubscriptionId());
            return new ResponseEntity(new SubscriptionResponse(subscription.getSubscriptionId(), subscription.getResponseQueue(), subscription.getExpiresAt()), HttpStatus.CREATED);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
