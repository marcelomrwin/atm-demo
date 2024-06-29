package com.redhat.atm;


import io.quarkus.security.identity.SecurityIdentity;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.resteasy.reactive.NoCache;

import java.util.Set;

@Path("/api/user")
@Slf4j
public class UserResource {
    @Inject
    SecurityIdentity identity;

    @Inject
    @ConfigProperty(name = "quarkus.application.name")
    protected String appName;

    @GET
    @Path("/logged")
    @Produces(MediaType.APPLICATION_JSON)
    @NoCache
    public User logged() {
        return new User(identity, appName);
    }

    @Getter
    public static class User {

        private final String userName;
        private final String applicationClient;
        private final Set<String> roles;

        User(SecurityIdentity identity, String appName) {
            this.userName = identity.getPrincipal().getName();
            this.roles = identity.getRoles();
            this.applicationClient = appName;
        }

    }
}
