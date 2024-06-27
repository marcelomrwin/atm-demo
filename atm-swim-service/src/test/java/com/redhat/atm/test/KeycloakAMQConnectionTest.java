package com.redhat.atm.test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.jms.Connection;
import jakarta.jms.Queue;
import jakarta.jms.Session;
import okhttp3.*;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;

public class KeycloakAMQConnectionTest {

    private String getJWT() throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(mediaType, "client_id=amq-broker&username=admin&password=password&grant_type=password&client_secret=G2I810Drfp1CtnfuJJLGZPtoAErcoVuf");
        Request request = new Request.Builder()
                .url("https://rhbk-nav-portugal.apps.ocp4.masales.cloud/realms/nav-portugal/protocol/openid-connect/token")
                .method("POST", body)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();
        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            throw new RuntimeException("Unexpected code " + response);
        }
        return response.body().string();
    }

    @Test
    public void connectTest() throws Exception {

        String response = getJWT();

        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> map = mapper.readValue(response, new TypeReference<>() {
        });

        String accessToken = map.get("access_token");
        System.out.println("Access token: [" + accessToken + "]");

        String brokerUrl = "tcp://localhost:61616";
        String queueName = "sample-queue";

        System.out.println("Criação da ConnectionFactory");
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerUrl);

        System.out.println("Criação da Conexão");
        Connection connection = connectionFactory.createConnection("admin-viewer", "password");
        connection.start();

        System.out.println("Criação da Sessão");
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        System.out.println("Criação da Fila");
        Queue queue = session.createQueue(queueName);
        System.out.println("Fila criada dinamicamente: " + queueName);

        session.close();
        connection.close();
    }
}
