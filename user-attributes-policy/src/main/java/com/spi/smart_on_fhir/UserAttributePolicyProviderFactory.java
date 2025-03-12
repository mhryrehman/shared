package com.spi.smart_on_fhir;


import org.jboss.logging.Logger;
import org.keycloak.Config.Scope;
import org.keycloak.authorization.AuthorizationProvider;
import org.keycloak.authorization.model.Policy;
import org.keycloak.authorization.policy.provider.PolicyProvider;
import org.keycloak.authorization.policy.provider.PolicyProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.representations.idm.authorization.PolicyRepresentation;

import java.util.HashMap;
import java.util.Map;


public class UserAttributePolicyProviderFactory implements PolicyProviderFactory<UserAttributePolicyRepresentation> {
    private static final Logger logger = Logger.getLogger(UserAttributePolicyProviderFactory.class);

    private UserAttributePolicyProvider provider = new UserAttributePolicyProvider(this::toRepresentation);

    @Override
    public PolicyProvider create(KeycloakSession session) {
        return provider;
    }

    @Override
    public void init(Scope config) {
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
    }

    @Override
    public void close() {
    }

    @Override
    public String getId() {
        return "user-attribute";
    }

    @Override
    public String getName() {
        return "User Attribute";
    }

    @Override
    public String getGroup() {
        return "Identity Based";
    }

    @Override
    public PolicyProvider create(AuthorizationProvider authorization) {
        return provider;
    }

    @Override
    public UserAttributePolicyRepresentation toRepresentation(Policy policy, AuthorizationProvider authorization) {
        UserAttributePolicyRepresentation representation = new UserAttributePolicyRepresentation();
        Map<String, String> config = policy.getConfig();

        representation.setAllowedAttributes(config.get("allowedAttributes"));
        representation.setUserAttributeKey(config.get("userAttributeKey"));
        representation.setMatchAll(Boolean.parseBoolean(config.getOrDefault("matchAll", "false"))); // Safe parsing

        return representation;
    }

    @Override
    public Class<UserAttributePolicyRepresentation> getRepresentationType() {
        return UserAttributePolicyRepresentation.class;
    }

    @Override
    public void onCreate(Policy policy, UserAttributePolicyRepresentation representation, AuthorizationProvider authorization) {
        Map<String, String> config = new HashMap<>(policy.getConfig());

        config.put("allowedAttributes",representation.getAllowedAttributes());
        config.put("userAttributeKey",representation.getUserAttributeKey());
        config.put("matchAll",String.valueOf(representation.isMatchAll()));

        policy.setConfig(config);
    }

    @Override
    public void onUpdate(Policy policy, UserAttributePolicyRepresentation representation, AuthorizationProvider authorization) {
        Map<String, String> config = new HashMap<>(policy.getConfig());

        config.put("allowedAttributes",representation.getAllowedAttributes());
        config.put("userAttributeKey",representation.getUserAttributeKey());
        config.put("matchAll",String.valueOf(representation.isMatchAll()));

        policy.setConfig(config);
    }

    @Override
    public void onImport(Policy policy, PolicyRepresentation representation, AuthorizationProvider authorization) {
        if (representation.getConfig() == null) {
            logger.warn("PolicyRepresentation config is null. Importing with an empty config.");
            policy.setConfig(new HashMap<>());
        } else {
            policy.setConfig(new HashMap<>(representation.getConfig())); // Ensure mutable map
        }
    }

}
