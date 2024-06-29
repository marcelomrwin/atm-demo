package com.redhat.atm.service;

import com.redhat.atm.model.dto.TopicResponse;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.List;

@Path("/api/subscription/v1")
@RegisterRestClient(configKey = "subscription")
public interface SubscriptionService {
    @GET
    @Path("/user/topics")
    List<TopicResponse> listTopics();
}
