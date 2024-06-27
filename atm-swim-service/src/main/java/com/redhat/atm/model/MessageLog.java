package com.redhat.atm.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Data
public class MessageLog {
    @Id
    private String messageId;
    @Column(nullable = false, updatable = false, columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private LocalDateTime createdAt;
    @Lob
    @Column(columnDefinition = "TEXT")
    private String payload;
    @ElementCollection
    @CollectionTable(name = "message_log_details", joinColumns = @JoinColumn(name = "message_id"))
    private Set<MessageLogDetail> messageLogDetails;
}
