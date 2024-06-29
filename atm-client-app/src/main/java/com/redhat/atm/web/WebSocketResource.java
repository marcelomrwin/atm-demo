package com.redhat.atm.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.redhat.atm.model.dto.ArrivalSequenceDTO;
import com.redhat.atm.service.ArrivalSequenceService;
import io.quarkus.vertx.ConsumeEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/sse/{clientApp}")
@ApplicationScoped
@Slf4j
public class WebSocketResource {
    Map<String, Session> sessions = new ConcurrentHashMap<>();

    @Inject
    ArrivalSequenceService arrivalSequenceService;

    @Inject
    ObjectMapper mapper;

    @ConsumeEvent("arrival-event")
    public void consumeEvent(ArrivalSequenceDTO arrivalSequence) {
        try {
            String eventJson = mapper.writeValueAsString(arrivalSequence);
            broadcast(eventJson);
        } catch (JsonProcessingException e) {
            log.error("Error during dispatch event {}", e.getMessage(), e);
        }
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("clientApp") String clientApp) {
//        broadcast("User " + clientApp + " joined");
        log.info("User {} joined", clientApp);
        sessions.put(clientApp, session);
        try {
            Optional<ArrivalSequenceDTO> lastEvent = arrivalSequenceService.getLastEvent();
            if (lastEvent.isPresent()) {
                session.getAsyncRemote().sendText(mapper.writeValueAsString(lastEvent.get()), result -> {
                    if (result.getException() != null) {
                        log.error("Unable to send last event: {}", result.getException().getMessage(), result.getException());
                    }
                });
            }
        } catch (JsonProcessingException e) {
            log.error("Error during dispatch last event {}", e.getMessage(), e);
        }
    }

    @OnClose
    public void onClose(Session session, @PathParam("clientApp") String clientApp) {
        sessions.remove(clientApp);
//        broadcast("User " + clientApp + " left");
        log.info("User {} left", clientApp);
    }

    @OnError
    public void onError(Session session, @PathParam("clientApp") String clientApp, Throwable throwable) {
        sessions.remove(clientApp);
//        broadcast("User " + clientApp + " left on error: " + throwable);
        log.error("User {} left on error {}", clientApp, throwable.getMessage(), throwable);
    }

    @OnMessage
    public void onMessage(String message, @PathParam("clientApp") String clientApp) {
//        broadcast(">> " + clientApp + ": " + message);
        log.info("User {} received message: {}", clientApp, message);
    }

    private void broadcast(String message) {
        sessions.values().forEach(s -> {
            s.getAsyncRemote().sendObject(message, result -> {
                if (result.getException() != null) {
                    log.error("Unable to send message: {}", result.getException().getMessage(), result.getException());
                }
            });
        });
    }
}
