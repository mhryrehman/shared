package org.keycloak.spi.util;

import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.jboss.logging.Logger;
import org.keycloak.connections.httpclient.HttpClientProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.util.JsonSerialization;

import java.io.IOException;
import java.util.Base64;
import java.util.Collections;
import java.util.Map;

/**
 * Utility class for making authenticated HTTP GET requests to external APIs.
 * Uses Keycloak's internal HttpClient for resource efficiency.
 */
public class HttpClientUtil {

    private static final Logger logger = Logger.getLogger(HttpClientUtil.class);

    /**
     * Calls an external API using Keycloak's HttpClient.
     *
     * @param session Keycloak session used to obtain HttpClient.
     * @param apiUrl  Full URL of the external API endpoint.
     * @param headers Map of headers to include in the request.
     * @return JSON response as a Map, or empty Map if failed.
     */
    public static Map<String, Object> callExternalApi(KeycloakSession session, String apiUrl, Map<String, String> headers) {
        if (apiUrl == null || apiUrl.isBlank()) {
            logger.error("API URL is null or blank. Cannot proceed with the external call.");
            return Collections.emptyMap();
        }

        CloseableHttpClient httpClient = session.getProvider(HttpClientProvider.class).getHttpClient();
        HttpGet request = new HttpGet(apiUrl);
        headers.forEach(request::setHeader);

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            int statusCode = response.getStatusLine().getStatusCode();

            logger.infof("External API call to %s responded with status: %d", apiUrl, statusCode);

            if (statusCode == 200) {
                return JsonSerialization.readValue(response.getEntity().getContent(), new TypeReference<>() {
                });
            } else {
                logger.warnf("Non-successful response from external API: HTTP %d", statusCode);
            }
        } catch (IOException e) {
            logger.errorf("An error occurred while calling external API: %s", apiUrl);
        }

        return Collections.emptyMap();
    }

    /**
     * Encodes a string using Base64 (typically used for Basic Auth).
     *
     * @param input The string to encode.
     * @return Base64-encoded version of the input.
     */
    public static String encodeBase64(String input) {
        return Base64.getEncoder().encodeToString(input.getBytes());
    }
}
