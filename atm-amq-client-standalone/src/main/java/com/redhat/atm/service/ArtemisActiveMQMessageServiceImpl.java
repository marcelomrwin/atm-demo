package com.redhat.atm.service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
public class ArtemisActiveMQMessageServiceImpl implements MessageService {

    @PostConstruct
    public void init() throws IOException {
        log.info("Using {} as Implementation for Interface {}", getClass().getSimpleName(), MessageService.class.getName());
    }

    @Override
    public void updateSubscriptions() throws Exception {
        log.info("Updating subscriptions");
    }
}
