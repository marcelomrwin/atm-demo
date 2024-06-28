package com.redhat.atm;


import io.quarkus.security.identity.SecurityIdentity;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jboss.resteasy.annotations.cache.NoCache;

import java.util.Map;
import java.util.Set;

@Path("/api/user")
@Slf4j
public class UserResource {
    @Inject
    SecurityIdentity identity;

    @GET
    @Path("/logged")
    @Produces(MediaType.APPLICATION_JSON)
    @NoCache
    public User logged() {
        return new User(identity);
    }

    @Getter
    public static class User {

        private final String userName;
        private final Set<String> roles;

        User(SecurityIdentity identity) {
            this.userName = identity.getPrincipal().getName();
            this.roles = identity.getRoles();
        }

    }
}
