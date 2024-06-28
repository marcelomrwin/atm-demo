package com.redhat.atm.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.redhat.atm.model.Subscription;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.artemis.api.core.ActiveMQException;
import org.apache.activemq.artemis.api.core.client.ActiveMQClient;
import org.apache.activemq.artemis.api.core.client.ClientSession;
import org.apache.activemq.artemis.api.core.client.ClientSessionFactory;
import org.apache.activemq.artemis.api.core.client.ServerLocator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ArtemisQueueManagementService {
    @Value("${jms.publisher.activemq.broker-url}")
    private String BROKER_URL;

    @Value("${jms.publisher.activemq.user}")
    private String BROKER_USERNAME;

    @Value("${jms.publisher.activemq.password}")
    private String BROKER_PASSWORD;

    private String BROKER_HTTP_URL;

    private static final List<String> EXCLUDED_QUEUES = Arrays.asList("DLQ", "ExpiryQueue", "$sys.mqtt.sessions", "activemq.notifications", "ARRIVAL_ORDER_TOPIC");

    @Autowired
    private SubscriptionService subscriptionService;

    @PostConstruct
    public void init() {
        BROKER_HTTP_URL = BROKER_URL.replace("tcp://", "http://").replace(":61616", ":8161");
        BROKER_HTTP_URL += "/console/jolokia/exec";
    }

    //curl -X POST --header "Content-Type: application/json" -k -u artemis:artemis http://artemis-swim-wconsj-0-svc-rte-nav-portugal.apps.ocp4.masales.cloud/console/jolokia/exec -d '{"type":"exec", "mbean": "org.apache.activemq.artemis:broker=\"amq-broker\"","operation": "listAddresses(java.lang.String)","arguments":["|"]}'
    public List<String> listQueues() throws JsonProcessingException {

        RestTemplate restTemplate = new RestTemplate();

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("type", "exec");
        requestBody.put("mbean", "org.apache.activemq.artemis:broker=\"amq-broker\"");
        requestBody.put("operation", "listAddresses(java.lang.String)");
        requestBody.put("arguments", new String[]{"|"});

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        headers.set("Authorization", createBasicAuthHeader(BROKER_USERNAME, BROKER_PASSWORD));

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                BROKER_HTTP_URL,
                HttpMethod.POST,
                entity,
                String.class
        );
        log.debug("Calling Artemis Endpoint {}", BROKER_HTTP_URL);
        String responseBody = response.getBody();
        log.debug("Response body: {}", responseBody);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(responseBody);

        String listString = jsonNode.path("value").textValue();
        StringTokenizer st = new StringTokenizer(listString, "|");
        List<String> tokens = new ArrayList<>();
        while (st.hasMoreTokens()) {
            tokens.add(st.nextToken());
        }

        List<String> queueList = tokens.stream()
                .filter(queue -> !EXCLUDED_QUEUES.contains(queue))
                .collect(Collectors.toList());

        return queueList;

    }

    public void syncrhonizeQueues() throws Exception {
        log.info("Synchronizing broker queues with database records");
        List<String> listQueues = listQueues();
        if (!listQueues.isEmpty()) {
            log.info("{} queues were found before synchronization", listQueues.size());
            try (ServerLocator serverLocator = ActiveMQClient.createServerLocator(BROKER_URL)) {
                try (ClientSessionFactory sessionFactory = serverLocator.createSessionFactory()) {
                    try (ClientSession session = sessionFactory.createSession(BROKER_USERNAME, BROKER_PASSWORD, false, true, true, false, 1)) {
                        session.start();
                        for (String queue : listQueues) {
                            Subscription subscription = subscriptionService.findById(queue);
                            if (subscription == null) {
                                log.warn("Requesting to delete queue {}", queue);
                                try {
                                    session.deleteQueue(queue);
                                    deleteAddressByConsole(queue);
                                } catch (ActiveMQException e) {
                                    log.error("Failed when trying to delete queue {}", queue, e);
                                    deleteAddressByConsole(queue);
                                }
                            }
                        }
                        session.commit();
                        log.info("End of synchronization");
                    }
                }
            }
        }
    }

    private void deleteAddressByConsole(String queue) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("type", "exec");
            requestBody.put("mbean", "org.apache.activemq.artemis:broker=\"amq-broker\"");
            requestBody.put("operation", "deleteAddress(java.lang.String,boolean)");
            requestBody.put("arguments", new String[]{queue, "true"});

            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);
            headers.set("Authorization", createBasicAuthHeader(BROKER_USERNAME, BROKER_PASSWORD));

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    BROKER_HTTP_URL,
                    HttpMethod.POST,
                    entity,
                    String.class
            );
            log.debug("Calling Artemis Endpoint {}", BROKER_HTTP_URL);
            String responseBody = response.getBody();
            log.debug("Response body: {}", responseBody);
            log.info("Address deleted by console: {}", queue);
        } catch (Exception e) {
            log.error("Error while deleting address", e);
        }
    }

    private String createBasicAuthHeader(String username, String password) {
        String auth = username + ":" + password;
        byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.US_ASCII));
        return "Basic " + new String(encodedAuth);
    }

}
