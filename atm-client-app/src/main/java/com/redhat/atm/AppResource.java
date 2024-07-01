package com.redhat.atm;

import com.redhat.atm.model.Subscription;
import com.redhat.atm.model.dto.SubscriptionResponseDTO;
import com.redhat.atm.model.dto.TopicResponse;
import com.redhat.atm.model.ed254.ArrivalSequence;
import com.redhat.atm.service.ArrivalSequenceService;
import com.redhat.atm.service.SubscriptionService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.List;

@Path("/api")
@Slf4j
public class AppResource {

    @RestClient
    protected SubscriptionService subscriptionService;

    @Inject
    ArrivalSequenceService arrivalSequenceService;

    @Inject
    JsonWebToken jwt;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String ping() {
        return "Pong";
    }

    @POST
    @Path("/callback")
    public Response callBack(ArrivalSequence arrivalSequence, @Context HttpHeaders headers, @QueryParam("correlationId") String correlationId) {
        log.debug("Callback called! ArrivalSequence: {}, CorrelationId: {}", arrivalSequence.toString(), correlationId);

        arrivalSequenceService.convertAndPublish(arrivalSequence);
        return Response.accepted().build();
    }

    @GET
    @Path("/topics")
    public List<TopicResponse> listTopicsToSubscribe() {
        return subscriptionService.listTopics();
    }

    @GET
    @Path("/subscriptions")
    public Response listSubscriptionsToSubscribe() {
        log.debug("Sending Token {}", jwt.getRawToken());
        List<Subscription> subscriptions = subscriptionService.listSubscriptions("Bearer " + jwt.getRawToken());

        if (subscriptions != null && !subscriptions.isEmpty()) {
            List<SubscriptionResponseDTO> responseDTOList = subscriptions.stream().map(sub -> {
                List<TopicResponse> topicResponses = sub.topicsOfInterest().stream().map(s -> new TopicResponse(s, s)).toList();
                return new SubscriptionResponseDTO(sub.subscriptionId(), sub.subscriber(), sub.createdAt(), sub.expiresAt(), sub.responseQueue(), sub.subscriberRoleName(), topicResponses);
            }).toList();
            return Response.ok(responseDTOList).build();
        }

        return Response.noContent().build();
    }
}
