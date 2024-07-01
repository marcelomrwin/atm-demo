package com.redhat.atm.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record Subscription(UUID subscriptionId, String subscriber, LocalDateTime createdAt,
                           LocalDateTime expiresAt, String responseQueue, String subscriberRoleName,
                           List<String> topicsOfInterest) {

}
