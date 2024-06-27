package com.redhat.atm.service;

import com.redhat.atm.model.Subscription;
import com.redhat.atm.repository.SubscriptionRepository;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.util.Config;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@Slf4j
public class SecretUpdaterAMQOperatorServiceImpl implements MessageService {

    private final Environment env;
    private final SubscriptionRepository subscriptionRepository;
    private final AddressConfigurationProducer addressConfigurationProducer;
    private final SecurityRolesConfigurationProducer securityRolesConfigurationProducer;
    private String namespace;
    private CoreV1Api coreV1Api;

    @Autowired
    public SecretUpdaterAMQOperatorServiceImpl(Environment env, SubscriptionRepository subscriptionRepository, AddressConfigurationProducer addressConfigurationProducer, SecurityRolesConfigurationProducer securityRolesConfigurationProducer) {
        this.env = env;
        this.subscriptionRepository = subscriptionRepository;
        this.addressConfigurationProducer = addressConfigurationProducer;
        this.securityRolesConfigurationProducer = securityRolesConfigurationProducer;
    }

    @PostConstruct
    public void init() throws IOException {
        log.info("Using {} as Implementation for Interface {}", getClass().getSimpleName(),MessageService.class.getName());
        namespace = env.getProperty("NAMESPACE", "default");
        ApiClient apiClient = Config.defaultClient();
        Configuration.setDefaultApiClient(apiClient);
        coreV1Api = new CoreV1Api(apiClient);
    }

    @Override
    public void updateSubscriptions()throws Exception {
        log.info("Retrieving all subscriptions");
        List<Subscription> subscriptions = subscriptionRepository.findAll();
        log.info("Updating the secret with {} subscriptions",subscriptions.size());
        log.info("Updating the secret for addresses and queues");
        addressConfigurationProducer.updateAddressConfig(subscriptions, coreV1Api, namespace);
        log.info("Secrets for addresses and queues were updated successfully");
        log.info("Updating security rules for queues");
        securityRolesConfigurationProducer.updateSecurityRoles(subscriptions,coreV1Api,namespace);
        log.info("Security rules for queues have been updated successfully");
    }

}
