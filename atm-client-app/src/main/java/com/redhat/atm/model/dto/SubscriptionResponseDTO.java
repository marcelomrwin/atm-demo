package com.redhat.atm.model.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record SubscriptionResponseDTO(UUID subscriptionId, String subscriber, LocalDateTime createdAt,
                                      LocalDateTime expiresAt, String responseQueue, String subscriberRoleName,
                                      List<TopicResponse> topicsOfInterest) {

}
