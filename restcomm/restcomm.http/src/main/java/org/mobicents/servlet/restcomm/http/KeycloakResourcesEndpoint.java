package org.mobicents.servlet.restcomm.http;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.keycloak.representations.adapters.config.BaseAdapterConfig;
import org.keycloak.util.JsonSerialization;
import org.mobicents.servlet.restcomm.identity.entities.IdentityModeEntity;
import org.mobicents.servlet.restcomm.identity.KeycloakConfigurator;
import org.mobicents.servlet.restcomm.identity.KeycloakConfigurator.CloudIdentityNotSet;

import com.google.gson.Gson;

@Path("/config")
public class KeycloakResourcesEndpoint extends AbstractEndpoint {

    private KeycloakConfigurator keycloakConfigurator;

    public KeycloakResourcesEndpoint() {
        super();
    }

    @PostConstruct
    private void init() {
        keycloakConfigurator = (KeycloakConfigurator) context.getAttribute(KeycloakConfigurator.class.getName());
    }

    @GET
    @Path("/mode")
    public Response getMode() {
        IdentityModeEntity modeEntity = new IdentityModeEntity();
        modeEntity.setMode(keycloakConfigurator.getMode());
        Gson gson = new Gson();
        return Response.ok(gson.toJson(modeEntity),MediaType.APPLICATION_JSON).build();
    }

    @GET
    @Path("/restcomm-ui.json")
    @Produces("application/json")
    public Response getRestcommUIConfig() throws IOException {
        BaseAdapterConfig config;
        try {
            config = keycloakConfigurator.getRestcommUIConfig();
        } catch (CloudIdentityNotSet e) {
            return Response.status(Status.NOT_FOUND).build();
        }
        return Response.ok(JsonSerialization.writeValueAsPrettyString(config), MediaType.APPLICATION_JSON).build();
    }

    @GET
    @Path("/restcomm-rvd-ui.json")
    @Produces("application/json")
    public Response getRestcommRvdUIConfig() throws IOException {
        BaseAdapterConfig config;
        try {
            config = keycloakConfigurator.getRestcommRvdUIConfig();
        } catch (CloudIdentityNotSet e) {
            return Response.status(Status.NOT_FOUND).build();
        }
        return Response.ok(JsonSerialization.writeValueAsPrettyString(config), MediaType.APPLICATION_JSON).build();
    }

}