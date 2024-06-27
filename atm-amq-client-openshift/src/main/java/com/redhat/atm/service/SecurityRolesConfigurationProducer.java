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
public class SecurityRolesConfigurationProducer {

    @Value("${application.security.roles.config.secret.name}")
    private String secretName;
    @Value("${application.security.roles.config.key.name}")
    private String securityRoleConfigName;
    @Value("${application.security.roles.config.admin.role.name}")
    private String adminRoleName;

    public V1Secret updateSecurityRoles(List<Subscription> subscriptions, CoreV1Api coreV1Api, String namespace) throws IOException, ApiException {
        log.info("Updating security roles");
        Properties properties = new PropertiesWithoutComments();
        for (Subscription subscription : subscriptions) {
            properties.put("securityRoles." + subscription.getResponseQueue() + "." + subscription.getSubscriberRoleName() + ".consume", Boolean.TRUE.toString());
            properties.put("securityRoles." + subscription.getResponseQueue() + "."+adminRoleName+".send", Boolean.TRUE.toString());
            properties.put("securityRoles." + subscription.getResponseQueue() + "."+adminRoleName+".manage", Boolean.TRUE.toString());
        }

        V1Secret secret = OpenshiftUtil.getBaseSecret(secretName);
        secret.setStringData(OpenshiftUtil.convertPropertiesToStringData(properties, securityRoleConfigName));

        log.info("Updating the secret {} using Kubernetes API", secretName);
        CoreV1Api.APIreplaceNamespacedSecretRequest replaceRequest = coreV1Api.replaceNamespacedSecret(secretName, namespace, secret);
        ApiResponse<V1Secret> replaceResponse = replaceRequest.executeWithHttpInfo();
        log.info("API Response Replace. Status: {}, Headers: {}, Data: {}", replaceResponse.getStatusCode(), replaceResponse.getHeaders(), replaceResponse.getData());
        return secret;
    }

}
