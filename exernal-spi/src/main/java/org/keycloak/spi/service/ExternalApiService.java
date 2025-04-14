package org.keycloak.spi.service;

import org.jboss.logging.Logger;
import org.keycloak.models.KeycloakSession;
import org.keycloak.spi.util.Constant;
import org.keycloak.spi.util.HttpClientUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Service responsible for securely calling an external API using Basic Authentication
 * and retrieving user-related claims to inject into tokens.
 */
public class ExternalApiService {

    private static final Logger logger = Logger.getLogger(ExternalApiService.class);

    private final String apiUrl;
    private final String clientId;
    private final String clientSecret;
    private final KeycloakSession session;

    public ExternalApiService(String apiUrl, String clientId, String clientSecret, KeycloakSession session) {
        this.apiUrl = apiUrl;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.session = session;
    }

    /**
     * Calls the external API and fetches data to be added to the token.
     *
     * @return A map of claims to inject into the token.
     */
    public Map<String, Object> fetchData(String userName) {
        try {
            // Build headers for the API call
            Map<String, String> headers = new HashMap<>();
            headers.put("Authorization", "Basic " + HttpClientUtil.encodeBase64(clientId + ":" + clientSecret));
            headers.put("Accept", "application/json");

            String finalUrl = apiUrl + Constant.QUERY_PARAM_USER_ID + userName;

            logger.debugf("Calling external API: %s", finalUrl);
            Map<String, Object> result = HttpClientUtil.callExternalApi(session, finalUrl, headers);

            if (result == null || result.isEmpty()) {
                logger.warn("External API returned no data.");
            } else {
                logger.debugf("External API returned %d keys.", result.size());
            }

            return result;

        } catch (Exception e) {
            logger.error("Failed to fetch data from external API", e);
            return Map.of(); // Return empty map on error
        }
    }
}
