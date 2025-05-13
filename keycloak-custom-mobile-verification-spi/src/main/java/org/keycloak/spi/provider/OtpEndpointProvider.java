package org.keycloak.spi.provider;

import org.keycloak.models.KeycloakSession;
import org.keycloak.services.resource.RealmResourceProvider;
import org.keycloak.spi.endpoint.OtpEndpoint;

public class OtpEndpointProvider implements RealmResourceProvider {

    private final KeycloakSession session;

    public OtpEndpointProvider(KeycloakSession session) {
        this.session = session;
    }

    @Override
    public Object getResource() {
        return new OtpEndpoint(session);
    }

    @Override
    public void close() {}
}

