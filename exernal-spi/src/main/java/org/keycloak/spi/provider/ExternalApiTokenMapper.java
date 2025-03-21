package org.keycloak.spi.provider;

import org.jboss.logging.Logger;
import org.keycloak.models.ClientSessionContext;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.ProtocolMapperModel;
import org.keycloak.models.UserSessionModel;
import org.keycloak.protocol.oidc.mappers.AbstractOIDCProtocolMapper;
import org.keycloak.protocol.oidc.mappers.OIDCAccessTokenMapper;
import org.keycloak.protocol.oidc.mappers.OIDCIDTokenMapper;
import org.keycloak.protocol.oidc.mappers.UserInfoTokenMapper;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.representations.AccessToken;
import org.keycloak.spi.service.ExternalApiService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A custom Keycloak Protocol Mapper that calls an external API using client credentials
 * and injects the returned data into the access token (and optionally ID token and user info).
 * <p>
 * Configuration options:
 * - External API URL
 * - Client ID
 * - Client Secret
 */
public class ExternalApiTokenMapper extends AbstractOIDCProtocolMapper
        implements OIDCAccessTokenMapper, OIDCIDTokenMapper, UserInfoTokenMapper {

    public static final String PROVIDER_ID = "oidc-external-api-token-mapper";
    // Configuration keys
    public static final String CONFIG_API_URL = "external.api.url";
    public static final String CONFIG_CLIENT_ID = "external.api.clientId";
    public static final String CONFIG_CLIENT_SECRET = "external.api.clientSecret";
    private static final Logger logger = Logger.getLogger(ExternalApiTokenMapper.class);
    private static final List<ProviderConfigProperty> configProperties = new ArrayList<>();

    static {
        ProviderConfigProperty apiUrlProperty = new ProviderConfigProperty();
        apiUrlProperty.setName(CONFIG_API_URL);
        apiUrlProperty.setLabel("External API URL");
        apiUrlProperty.setHelpText("Enter the URL of the external API that provides user data.");
        apiUrlProperty.setType(ProviderConfigProperty.STRING_TYPE);
        configProperties.add(apiUrlProperty);

        ProviderConfigProperty clientIdProperty = new ProviderConfigProperty();
        clientIdProperty.setName(CONFIG_CLIENT_ID);
        clientIdProperty.setLabel("Client ID");
        clientIdProperty.setHelpText("Enter the Client ID for authenticating with the external API.");
        clientIdProperty.setType(ProviderConfigProperty.STRING_TYPE);
        configProperties.add(clientIdProperty);

        ProviderConfigProperty clientSecretProperty = new ProviderConfigProperty();
        clientSecretProperty.setName(CONFIG_CLIENT_SECRET);
        clientSecretProperty.setLabel("Client Secret");
        clientSecretProperty.setHelpText("Enter the Client Secret for authenticating with the external API.");
        clientSecretProperty.setType(ProviderConfigProperty.PASSWORD);
        configProperties.add(clientSecretProperty);
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return configProperties;
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }

    @Override
    public String getDisplayType() {
        return "External API Token Mapper";
    }

    @Override
    public String getDisplayCategory() {
        return "Token Mapper";
    }

    @Override
    public String getHelpText() {
        return "Calls an external API and adds the fetched data as claims to the token.";
    }

    /**
     * Transforms the access token by injecting additional claims fetched from an external API.
     */
    @Override
    public AccessToken transformAccessToken(AccessToken token,
                                            ProtocolMapperModel mappingModel,
                                            KeycloakSession session,
                                            UserSessionModel userSession,
                                            ClientSessionContext clientSessionCtx) {
        String apiUrl = mappingModel.getConfig().get(CONFIG_API_URL);
        String clientId = mappingModel.getConfig().get(CONFIG_CLIENT_ID);
        String clientSecret = mappingModel.getConfig().get(CONFIG_CLIENT_SECRET);

        if (apiUrl == null || clientId == null || clientSecret == null) {
            logger.warn("External API Token Mapper: API URL, Client ID, or Client Secret is not configured. Skipping external API call.");
            return token;
        }

        try {
            ExternalApiService apiService = new ExternalApiService(apiUrl, clientId, clientSecret, session);
            Map<String, Object> externalData = apiService.fetchData();

            if (externalData == null || externalData.isEmpty()) {
                logger.info("External API Token Mapper: No data returned from external API.");
                return token;
            }

            logger.infof("External API Token Mapper: Adding %d claims to the token.", externalData.size());

            for (Map.Entry<String, Object> entry : externalData.entrySet()) {
                token.getOtherClaims().put(entry.getKey(), entry.getValue());
            }
        } catch (Exception e) {
            logger.error("External API Token Mapper: Error calling external API", e);
        }

        return token;
    }
}
