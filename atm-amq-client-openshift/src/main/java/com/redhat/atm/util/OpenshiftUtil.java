package com.redhat.atm.util;

import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.openapi.models.V1Secret;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class OpenshiftUtil {
    public static V1Secret getBaseSecret(String secretName) {
        V1Secret v1Secret = new V1Secret();
        v1Secret.setKind("Secret");
        v1Secret.setApiVersion("v1");
        v1Secret.setType("Opaque");
        V1ObjectMeta metadata = new V1ObjectMeta();
        metadata.setName(secretName);
        v1Secret.setMetadata(metadata);
        return v1Secret;
    }

    public static Map<String, String> convertPropertiesToStringData(Properties properties, String propertyName) throws IOException {
        StringWriter writer = new StringWriter();
        properties.store(writer, null);
        String propertiesString = writer.toString();

        Map<String, String> stringData = new HashMap<>();
        stringData.put(propertyName, propertiesString);
        return stringData;
    }
}
