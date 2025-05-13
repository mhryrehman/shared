package org.keycloak.spi.factory;

import org.keycloak.models.KeycloakSession;
import org.keycloak.services.resource.RealmResourceProvider;
import org.keycloak.services.resource.RealmResourceProviderFactory;
import org.keycloak.spi.provider.OtpEndpointProvider;

public class OtpEndpointProviderFactory implements RealmResourceProviderFactory {

    public static final String ID = "otp";

    @Override
    public RealmResourceProvider create(KeycloakSession session) {
        return new OtpEndpointProvider(session);
    }

    @Override
    public void init(org.keycloak.Config.Scope config) {}

    @Override
    public void postInit(org.keycloak.models.KeycloakSessionFactory factory) {}

    @Override
    public void close() {}

    @Override
    public String getId() {
        return ID;
    }
}
