package com.keycloak.auth.phone;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.jbosslog.JBossLog;
import org.apache.http.HttpStatus;
import org.jboss.logging.Logger;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.Authenticator;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

import java.util.HashMap;
import java.util.Map;

/**
 * Custom Keycloak Authenticator that verifies if the user's phone number is marked as verified
 * through a user attribute ("PhoneVerified") before allowing token issuance in the Direct Access Grant flow.
 */
@JBossLog
public class VerifyPhoneAuthenticator implements Authenticator {

    private static final Logger logger = Logger.getLogger(VerifyPhoneAuthenticator.class);

    private static final String ATTRIBUTE_PHONE_VERIFIED = "PhoneVerified";

    /**
     * Authentication logic for verifying the "PhoneVerified" user attribute during Direct Access Grant.
     */
    @Override
    public void authenticate(AuthenticationFlowContext context) {
        UserModel user = context.getUser();
        String username = user.getUsername();
        String isPhoneVerified = user.getFirstAttribute(ATTRIBUTE_PHONE_VERIFIED);

        logger.infof("Authenticating user '%s'. PhoneVerified = %s", username, isPhoneVerified);

        if (!"true".equalsIgnoreCase(isPhoneVerified)) {
            logger.warnf("User '%s' failed phone verification. PhoneVerified = %s", username, isPhoneVerified);
            context.failureChallenge(
                    AuthenticationFlowError.INVALID_CREDENTIALS,
                    buildErrorResponse("invalid_grant", "Account is not fully set up.", HttpStatus.SC_BAD_REQUEST)
            );
            return;
        }

        context.success();
    }

    /**
     * Not used in Direct Access Grant flows.
     */
    @Override
    public void action(AuthenticationFlowContext context) {
        // No interactive action required
    }

    /**
     * Requires an identified user before execution.
     */
    @Override
    public boolean requiresUser() {
        return true;
    }

    /**
     * Confirms this authenticator applies if the user has an email set (optional).
     */
    @Override
    public boolean configuredFor(KeycloakSession session, RealmModel realm, UserModel user) {
        return user.getEmail() != null;
    }

    /**
     * No required actions to be set on the user.
     */
    @Override
    public void setRequiredActions(KeycloakSession session, RealmModel realm, UserModel user) {
        // No post-auth required actions
    }

    /**
     * Cleanup logic (not needed).
     */
    @Override
    public void close() {
        // No resources to close
    }

    /**
     * Constructs a JSON error response.
     */
    private Response buildErrorResponse(String errorCode, String errorDescription, int httpStatus) {
        Map<String, String> error = new HashMap<>();
        error.put("error", errorCode);
        error.put("error_description", errorDescription);

        return Response.status(httpStatus)
                .entity(error)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
