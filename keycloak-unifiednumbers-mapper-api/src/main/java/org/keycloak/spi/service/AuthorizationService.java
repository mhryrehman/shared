package org.keycloak.spi.service;

import org.jboss.logging.Logger;
import org.keycloak.authorization.AuthorizationProvider;
import org.keycloak.authorization.Decision;
import org.keycloak.authorization.attribute.Attributes;
import org.keycloak.authorization.identity.Identity;
import org.keycloak.authorization.model.Resource;
import org.keycloak.authorization.model.ResourceServer;
import org.keycloak.authorization.permission.ResourcePermission;
import org.keycloak.authorization.permission.evaluator.PermissionEvaluator;
import org.keycloak.authorization.policy.evaluation.Evaluation;
import org.keycloak.authorization.policy.evaluation.EvaluationContext;
import org.keycloak.models.*;

import java.util.*;

/**
 * Service responsible for fetching user permissions from Keycloak's authorization services.
 */
public class AuthorizationService {

    private static final Logger logger = Logger.getLogger(AuthorizationService.class);

    /**
     * Fetches the list of permissions (resource ID and name) granted to the authenticated user.
     *
     * @param session          the current Keycloak session
     * @param userSession      the user's session model
     * @param clientSessionCtx the client session context
     * @return a list of permissions where each permission is represented as a map containing "id" and "name"
     */
    public List<Map<String, String>> fetchUserPermissions(KeycloakSession session, UserSessionModel userSession, ClientSessionContext clientSessionCtx) {
        List<Map<String, String>> userPermissions = new ArrayList<>();
        try {
            ClientModel client = clientSessionCtx.getClientSession().getClient();
            AuthorizationProvider authorizationProvider = session.getProvider(AuthorizationProvider.class);
            ResourceServer resourceServer = authorizationProvider.getStoreFactory().getResourceServerStore().findById(client.getId());

            if (resourceServer != null) {
                Identity identity = createIdentity(userSession);
                EvaluationContext evaluationContext = createEvaluationContext(identity);

                List<ResourcePermission> permissions = new ArrayList<>();
                List<Resource> resources = authorizationProvider.getStoreFactory()
                        .getResourceStore()
                        .findByResourceServer(resourceServer);
                for (Resource resource : resources) {
                    permissions.add(new ResourcePermission(resource, resource.getScopes(), resourceServer));
                }

                Decision<Evaluation> decision = getEvaluationDecision(userPermissions);

                PermissionEvaluator evaluator = authorizationProvider.evaluators()
                        .from(permissions, evaluationContext);

                evaluator.evaluate(decision);
            }
        } catch (Exception e) {
            logger.error("AuthorizationService: Error fetching user permissions", e);
        }
        logger.info("User permission: " + userPermissions);
        return userPermissions;
    }

    /**
     * Creates a Decision object that processes evaluations and collects unique permissions.
     *
     * @param userPermissions the list where granted permissions will be added
     * @return a Decision handler for permission evaluations
     */
    private Decision<Evaluation> getEvaluationDecision(List<Map<String, String>> userPermissions) {
        Set<String> processedResourceIds = new HashSet<>();
        return new Decision<>() {
            @Override
            public void onDecision(Evaluation evaluation) {
                if (evaluation.getEffect() == Effect.PERMIT) {
                    ResourcePermission permission = evaluation.getPermission();
                    if (permission != null && permission.getResource() != null) {
                        String resourceId = permission.getResource().getId();
                        String resourceName = permission.getResource().getName();

                        if (processedResourceIds.add(resourceId)) { // only add if new
                            Map<String, String> permissionObj = new HashMap<>();
                            permissionObj.put("id", resourceId);
                            permissionObj.put("name", resourceName);
                            userPermissions.add(permissionObj);
                        }
                    }
                }
            }

            @Override
            public void onError(Throwable cause) {
                logger.error("Error during permission evaluation", cause);
            }

            @Override
            public void onComplete() {
                logger.info("Permission evaluation completed.");
            }
        };
    }

    /**
     * Builds an Identity object for the current user session, including user roles as attributes.
     *
     * @param userSession the user's session model
     * @return an Identity representing the authenticated user
     */
    private Identity createIdentity(UserSessionModel userSession) {
        return new Identity() {
            @Override
            public String getId() {
                return userSession.getUser().getId();
            }

            @Override
            public Attributes getAttributes() {
                Map<String, Collection<String>> attributes = new HashMap<>();

                userSession.getUser().getRoleMappingsStream()
                        .filter(role -> role.getContainer() instanceof RealmModel)
                        .forEach(role -> attributes.computeIfAbsent("kc.realm.roles", k -> new ArrayList<>()).add(role.getName()));

                for (RoleModel role : userSession.getUser().getRoleMappingsStream().toList()) {
                    if (role.getContainer() instanceof ClientModel) {
                        String clientId = ((ClientModel) role.getContainer()).getClientId();
                        attributes.computeIfAbsent("kc.client." + clientId + ".roles", k -> new ArrayList<>()).add(role.getName());
                    }
                }

                return Attributes.from(attributes);
            }
        };
    }

    /**
     * Builds an EvaluationContext based on the provided Identity.
     *
     * @param identity the user's identity
     * @return an EvaluationContext containing user identity and attributes
     */
    private EvaluationContext createEvaluationContext(Identity identity) {
        return new EvaluationContext() {
            @Override
            public Identity getIdentity() {
                return identity;
            }

            @Override
            public Attributes getAttributes() {
                return identity.getAttributes();
            }
        };
    }
}
