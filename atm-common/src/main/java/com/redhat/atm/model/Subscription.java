package com.redhat.atm.model;

import com.redhat.atm.model.ed254.TopicType;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Data
public class Subscription {
    @Id
    private UUID subscriptionId;
    @Column(nullable = false)
    private String subscriber;
    @Column(nullable = false, updatable = false, columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private LocalDateTime createdAt;
    @Column(nullable = false, updatable = false, columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private LocalDateTime expiresAt;
    @Column(nullable = false)
    private String responseQueue;
    @Column(nullable = false)
    private String subscriberRoleName;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "subscription_topics", joinColumns = @JoinColumn(name = "subscription_id"))
    private List<TopicType> topicsOfInterest;

    public List<TopicType> getTopicsOfInterest() {
        if (topicsOfInterest == null) {
            topicsOfInterest = new ArrayList<>();
        }
        return topicsOfInterest;
    }
}
