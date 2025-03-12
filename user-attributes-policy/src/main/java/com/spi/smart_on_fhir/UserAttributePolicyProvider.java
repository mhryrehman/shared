package com.spi.smart_on_fhir;

import org.jboss.logging.Logger;
import org.keycloak.authorization.AuthorizationProvider;
import org.keycloak.authorization.attribute.Attributes;
import org.keycloak.authorization.model.Policy;
import org.keycloak.authorization.policy.evaluation.Evaluation;
import org.keycloak.authorization.policy.provider.PolicyProvider;

import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class UserAttributePolicyProvider implements PolicyProvider {

    private static final Logger logger = Logger.getLogger(UserAttributePolicyProvider.class);
    private final BiFunction<Policy, AuthorizationProvider, UserAttributePolicyRepresentation> representationFunction;

    public UserAttributePolicyProvider(BiFunction<Policy, AuthorizationProvider, UserAttributePolicyRepresentation> representationFunction) {
        this.representationFunction = representationFunction;
    }

    @Override
    public void close() {
    }

    @Override
    public void evaluate(Evaluation evaluation) {
        Policy policy = evaluation.getPolicy();
        Map<String, String> config = policy.getConfig();
        Attributes identityAttributes = evaluation.getContext().getIdentity().getAttributes();

        // Extract policy config values
        String allowedAttributesConfig = config.get("allowedAttributes");
        String userAttributeKey = config.get("userAttributeKey");
        boolean matchAll = Boolean.parseBoolean(config.getOrDefault("matchAll", "false"));

        logger.infof("Policy Config -> Allowed Attributes: %s, User Attribute Key: %s, Match All: %b",
                allowedAttributesConfig, userAttributeKey, matchAll);

        // Extract user attributes and allowed attributes
        Set<String> userAttributes = extractValues(identityAttributes, userAttributeKey);
        Set<String> allowedAttributes = splitString(allowedAttributesConfig);

        logger.infof("User Attributes: %s", userAttributes);
        logger.infof("Allowed Attributes: %s", allowedAttributes);

        if (allowedAttributes.isEmpty()) {
            logger.warn("Allowed attributes are not set in policy config. Denying access.");
            evaluation.deny();
            return;
        }

        // Perform attribute comparison
        if (hasCommonElement(userAttributes, allowedAttributes, matchAll)) {
            logger.info("User attribute matched. Granting access.");
            evaluation.grant();
        } else {
            logger.info("No matching user attribute found. Denying access.");
            evaluation.deny();
        }
    }

    /**
     * Extracts user attribute values from Keycloak identity attributes.
     */
    public static Set<String> extractValues(Attributes identityAttributes, String attributeName) {
        Attributes.Entry entry = identityAttributes.getValue(attributeName);
        if (entry == null || entry.isEmpty()) {
            return Set.of();
        }

        return IntStream.range(0, entry.size())
                .mapToObj(entry::asString)
                .collect(Collectors.toSet());
    }

    /**
     * Splits a comma-separated string into a Set of values.
     */
    public static Set<String> splitString(String input) {
        if (input == null || input.isEmpty()) {
            return Set.of();
        }

        return Arrays.stream(input.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());
    }

    /**
     * Checks if there is a match between user attributes and allowed attributes.
     */
    public static boolean hasCommonElement(Set<String> userAttributes, Set<String> allowedAttributes, boolean matchAll) {
        if (userAttributes.isEmpty() || allowedAttributes.isEmpty()) {
            return false;
        }

        return matchAll
                ? userAttributes.containsAll(allowedAttributes)  // All must match
                : allowedAttributes.stream().anyMatch(userAttributes::contains);  // At least one must match
    }
}
