package com.redhat.atm.service;

import com.redhat.atm.model.dto.ArrivalSequenceDTO;
import com.redhat.atm.model.dto.EntryDTO;
import com.redhat.atm.model.ed254.ArrivalSequence;
import io.vertx.core.eventbus.EventBus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.websocket.Session;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

@ApplicationScoped
@Slf4j
public class ArrivalSequenceService {

    Stack<ArrivalSequenceDTO> arrivalSequenceStack = new Stack<>();

    @Inject
    EventBus bus;

    public void convertAndPublish(ArrivalSequence arrivalSequence) {

        SortedSet<EntryDTO> entryDTOS = arrivalSequence.entry().stream().map(entry -> {
            return new EntryDTO(entry.flightID().arcid(), entry.aircraft().arctyp(), entry.flightID().eobt(), entry.handling().seqnr());
        }).collect(Collectors.toCollection(TreeSet::new)).descendingSet();

        log.debug("Publishing arrival sequence: {}", entryDTOS.size());

        ArrivalSequenceDTO dto = new ArrivalSequenceDTO(arrivalSequence.topic(), arrivalSequence.messageMeta().managedAerodrome(), arrivalSequence.messageMeta().publicationTime(), entryDTOS);
        arrivalSequenceStack.push(dto);
        bus.publish("arrival-event", dto);
    }

    public Optional<ArrivalSequenceDTO> getLastEvent() {
        if (!arrivalSequenceStack.isEmpty()) {
            ArrivalSequenceDTO dto = arrivalSequenceStack.peek();
            return Optional.of(dto);
        }
        return Optional.empty();
    }

}
