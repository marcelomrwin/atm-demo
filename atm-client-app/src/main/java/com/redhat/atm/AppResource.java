package com.redhat.atm;

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
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.List;

@Path("/api")
@Slf4j
public class AppResource {

    @RestClient
    protected SubscriptionService subscriptionService;

    @Inject
    ArrivalSequenceService arrivalSequenceService;

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
}
