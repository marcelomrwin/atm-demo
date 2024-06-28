package com.redhat.atm;

import com.redhat.atm.model.ed254.ArrivalSequence;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import lombok.extern.slf4j.Slf4j;

@Path("/api")
@Slf4j
public class AppResource {
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String ping() {
        return "Pong";
    }

    @POST
    @Path("/callback")
    public Response callBack(ArrivalSequence arrivalSequence, @Context HttpHeaders headers, @QueryParam("correlationId") String correlationId) {
        log.info("Callback called!");
        log.info("ArrivalSequence: {}",arrivalSequence.toString());
        log.info("HTTP Headers:");
        MultivaluedMap<String, String> requestHeaders = headers.getRequestHeaders();
        requestHeaders.keySet().forEach(k->{
            log.info("Header: {}, Value: {}",k,requestHeaders.get(k));
        });

        return Response.accepted().build();
    }
}
