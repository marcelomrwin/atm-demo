package com.redhat.atm.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record SubscriptionResponse(UUID subscriptionId, String responseQueue, LocalDateTime subscriptionExp) {
}
