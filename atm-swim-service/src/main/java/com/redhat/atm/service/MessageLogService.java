package com.redhat.atm.service;

import com.redhat.atm.model.MessageLog;
import com.redhat.atm.repository.MessageLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MessageLogService {
    private final MessageLogRepository messageLogRepository;

    @Autowired
    public MessageLogService(MessageLogRepository messageLogRepository) {
        this.messageLogRepository = messageLogRepository;
    }

    @Transactional
    public MessageLog save(MessageLog messageLog) {
        return messageLogRepository.save(messageLog);
    }

    @Transactional(readOnly = true)
    public List<MessageLog> listAll() {
        return messageLogRepository.findAll();
    }

    @Transactional(readOnly = true)
    public MessageLog findById(String messageId) {
        return messageLogRepository.findById(messageId).orElse(null);
    }
}
