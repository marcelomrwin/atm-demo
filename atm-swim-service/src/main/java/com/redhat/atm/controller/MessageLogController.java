package com.redhat.atm.controller;

import com.redhat.atm.model.MessageLog;
import com.redhat.atm.service.MessageLogService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Tag(
        name = "SWIM",
        description = "ATM SWIM MESSAGE LOG API"
)
@RequestMapping({"/api/messagelog/v1"})
@Slf4j
public class MessageLogController {

    private final MessageLogService messageLogService;

    @Autowired
    public MessageLogController(MessageLogService messageLogService) {
        this.messageLogService = messageLogService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @RequestMapping("/list")
    public ResponseEntity<?> listMessageLog() {
        List<MessageLog> messageLogs = messageLogService.listAll();
        return ResponseEntity.ok(messageLogs);
    }
}
