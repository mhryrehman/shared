package com.keycloak.auth.phone;

import org.jboss.logging.Logger;
import org.keycloak.Config;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.AuthenticatorFactory;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderConfigProperty;

import java.util.Collections;
import java.util.List;

/**
 * Factory for the {@link VerifyPhoneAuthenticator}, used in the Direct Access Grant flow
 * to enforce phone number verification via a user attribute.
 *
 * Registers the authenticator with Keycloak using a unique provider ID.
 */
public class VerifyPhoneAuthenticatorFactory implements AuthenticatorFactory {

    public static final String PROVIDER_ID = "phone-verification-authenticator";
    private static final VerifyPhoneAuthenticator SINGLETON = new VerifyPhoneAuthenticator();

    /**
     * Returns the unique provider ID used to register this authenticator in Keycloak.
     */
    @Override
    public String getId() {
        return PROVIDER_ID;
    }

    /**
     * Returns the display name shown in the Admin Console.
     */
    @Override
    public String getDisplayType() {
        return "Verify Phone Number";
    }

    /**
     * Reference category (can be used for grouping related authenticators, not used here).
     */
    @Override
    public String getReferenceCategory() {
        return null; // Not OTP-based
    }

    /**
     * Defines available requirement options: REQUIRED, ALTERNATIVE, DISABLED.
     */
    @Override
    public AuthenticationExecutionModel.Requirement[] getRequirementChoices() {
        return REQUIREMENT_CHOICES;
    }

    /**
     * Indicates if the authenticator has admin-configurable properties.
     */
    @Override
    public boolean isConfigurable() {
        return false;
    }

    /**
     * Indicates if the user can configure this authenticator (not applicable here).
     */
    @Override
    public boolean isUserSetupAllowed() {
        return false;
    }

    /**
     * Help text displayed in Admin Console.
     */
    @Override
    public String getHelpText() {
        return "Verifies that the user's phone number is marked as verified before allowing login via Direct Access Grant.";
    }

    /**
     * No configurable properties for this authenticator.
     */
    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return Collections.emptyList();
    }

    /**
     * Creates an instance of the authenticator.
     */
    @Override
    public Authenticator create(KeycloakSession session) {
        return SINGLETON;
    }

    /**
     * Initialization hook (not used).
     */
    @Override
    public void init(Config.Scope config) {
        // No initialization needed
    }

    /**
     * Post-initialization hook (not used).
     */
    @Override
    public void postInit(KeycloakSessionFactory factory) {
        // No post-init actions
    }

    /**
     * Cleanup when factory is closed (not used).
     */
    @Override
    public void close() {
        // No cleanup required
    }
}
