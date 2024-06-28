package com.redhat.atm.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.redhat.atm.service.ArtemisQueueManagementService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(
        name = "SWIM",
        description = "ATM SWIM QUEUE MANAGEMENT API"
)
@RequestMapping({"/api/queue/management/v1"})
@Slf4j
public class QueueManagementController {

    private final ArtemisQueueManagementService artemisQueueManagementService;

    @Autowired
    public QueueManagementController(ArtemisQueueManagementService artemisQueueManagementService) {
        this.artemisQueueManagementService = artemisQueueManagementService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getQueues() {
        try {
            return ResponseEntity.ok(artemisQueueManagementService.listQueues());
        } catch (JsonProcessingException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping
    @RequestMapping("/admin/sync")
    public ResponseEntity<?> synchronizeQueues(){
        try {
            artemisQueueManagementService.syncrhonizeQueues();
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}
