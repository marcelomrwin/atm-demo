package com.redhat.atm.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record UnSubscriptionRequest(String subscriptionId) {
}
