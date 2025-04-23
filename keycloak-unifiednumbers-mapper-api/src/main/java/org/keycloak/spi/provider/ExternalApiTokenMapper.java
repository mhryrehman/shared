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
import org.keycloak.spi.util.Constant;

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

        if (apiUrl == null) {
            logger.warn("External API Token Mapper: API URL is not configured. Skipping external API call.");
            return token;
        }

        try {
            ExternalApiService apiService = new ExternalApiService(apiUrl, session);
            if (null != userSession && null != userSession.getUser()) {
                Map<String, Object> externalData = apiService.fetchData(userSession.getUser().getUsername());

                if (externalData == null || externalData.isEmpty()) {
                    logger.info("External API Token Mapper: No data returned from external API.");
                    return token;
                }

                logger.infof("External API Token Mapper: Adding claims to the token.");
                token.getOtherClaims().put(Constant.UNIFIED_NATIONAL_NUMBERS, externalData.get(Constant.UNIFIED_NATIONAL_NUMBERS));
            }

        } catch (Exception e) {
            logger.error("External API Token Mapper: Error calling external API", e);
        }

        return token;
    }
}
