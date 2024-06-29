package com.redhat.atm.model.dto;

import java.time.LocalDateTime;
import java.util.SortedSet;

public record ArrivalSequenceDTO(String topic, String managedAerodrome, LocalDateTime publicationDate, SortedSet<EntryDTO> entries) {
}
