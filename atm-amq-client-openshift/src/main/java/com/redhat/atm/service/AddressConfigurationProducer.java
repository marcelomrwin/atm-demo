package com.redhat.atm.service;

import com.redhat.atm.model.Subscription;
import com.redhat.atm.util.OpenshiftUtil;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.ApiResponse;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Secret;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

@Component
@Slf4j
public class AddressConfigurationProducer {

    @Value("${application.address.config.secret.name}")
    private String secretName;
    @Value("${application.address.config.key.name}")
    private String addressConfigName;

    public V1Secret updateAddressConfig(List<Subscription> subscriptionList, CoreV1Api coreV1Api, String namespace) throws IOException, ApiException {
        Properties properties = new PropertiesWithoutComments();
        for (Subscription subscription : subscriptionList) {
            properties.put("addressConfigurations." + subscription.getResponseQueue() + ".routingTypes", "ANYCAST");
            properties.put("addressConfigurations." + subscription.getResponseQueue() + ".queueConfigs." + subscription.getResponseQueue() + ".address", subscription.getResponseQueue());
            properties.put("addressConfigurations." + subscription.getResponseQueue() + ".queueConfigs." + subscription.getResponseQueue() + ".routingType", "ANYCAST");
        }

        V1Secret secret = OpenshiftUtil.getBaseSecret(secretName);
        secret.setStringData(OpenshiftUtil.convertPropertiesToStringData(properties, addressConfigName));

        log.info("Updating the secret {} using Kubernetes API", secretName);
        CoreV1Api.APIreplaceNamespacedSecretRequest replaceRequest = coreV1Api.replaceNamespacedSecret(secretName, namespace, secret);
        ApiResponse<V1Secret> replaceResponse = replaceRequest.executeWithHttpInfo();
        log.info("API Response Replace. Status: {}, Headers: {}, Data: {}", replaceResponse.getStatusCode(), replaceResponse.getHeaders(), replaceResponse.getData());
        return secret;
    }
}
