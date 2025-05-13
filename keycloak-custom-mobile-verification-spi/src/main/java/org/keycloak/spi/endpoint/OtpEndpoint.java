package org.keycloak.spi.endpoint;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;
import org.keycloak.common.util.SecretGenerator;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.spi.util.Constant;
import org.keycloak.utils.StringUtil;

import java.util.Map;

@Path("/otp")
public class OtpEndpoint {

    private static final Logger logger = Logger.getLogger(OtpEndpoint.class);
    private final KeycloakSession session;

    public OtpEndpoint(KeycloakSession session) {
        this.session = session;
    }

    @POST
    @Path("/send")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response sendOtp(Map<String, String> payload) {
        String username = payload.get("username");
        if (StringUtil.isNullOrEmpty(username)) {
            return errorResponse(Response.Status.BAD_REQUEST, "Username is required");
        }

        UserModel user = getUserModel(username);
        if (user == null) {
            return errorResponse(Response.Status.NOT_FOUND, "User not found");
        }

        String otp = SecretGenerator.getInstance().randomString(Constant.OTP_LENGTH, SecretGenerator.DIGITS);
        long expiry = System.currentTimeMillis() + (Constant.TTL_VAL * 1000L);

        user.setSingleAttribute(Constant.OTP, otp);
        user.setSingleAttribute(Constant.TTL, Long.toString(expiry));

        logger.infof("Generated OTP for %s", username); // Do not log OTP in production

        // TODO: Send OTP using SMS/email API

        return Response.ok(Map.of("message", "OTP sent successfully")).build();
    }

    @POST
    @Path("/validate")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response validateOtp(Map<String, String> payload) {
        String username = payload.get("username");
        String otp = payload.get("otp");

        if (StringUtil.isNullOrEmpty(username) || StringUtil.isNullOrEmpty(otp)) {
            return errorResponse(Response.Status.BAD_REQUEST, "Username and OTP are required");
        }

        UserModel user = getUserModel(username);
        if (user == null) {
            return errorResponse(Response.Status.NOT_FOUND, "User not found");
        }

        String storedOtp = user.getFirstAttribute(Constant.OTP);
        String expiryStr = user.getFirstAttribute(Constant.TTL);

        if (StringUtil.isNullOrEmpty(storedOtp) || StringUtil.isNullOrEmpty(expiryStr)) {
            logger.warnf("Missing OTP or TTL for user: %s", username);
            return errorResponse(Response.Status.UNAUTHORIZED, "OTP is not yet generated.");
        }

        if (!otp.equals(storedOtp)) {
            logger.warnf("Invalid OTP entered by user: %s", username);
            return errorResponse(Response.Status.UNAUTHORIZED, "The OTP you provided is incorrect. Please try again.");
        }

        try {
            long expiry = Long.parseLong(expiryStr);
            if (System.currentTimeMillis() > expiry) {
                logger.warnf("Expired OTP for user: %s", username);
                return errorResponse(Response.Status.UNAUTHORIZED, "Expired OTP");
            }
        } catch (NumberFormatException e) {
            logger.errorf("Invalid expiry timestamp for OTP of user: %s", username);
            return errorResponse(Response.Status.UNAUTHORIZED, "Something went wrong with OTP validation. Please try again.");
        }

        user.removeAttribute(Constant.OTP);
        user.removeAttribute(Constant.TTL);
        user.setSingleAttribute(Constant.PHONE_VERIFIED, "true");

        logger.infof("OTP validated successfully for user: %s", username);

        return Response.ok(Map.of("message", "OTP is valid")).build();
    }

    private UserModel getUserModel(String userName) {
        RealmModel realm = session.getContext().getRealm();
        UserModel user = session.users().getUserByUsername(realm, userName);
        if (user == null) {
            user = session.users().getUserByEmail(realm, userName);
        }
        return user;
    }

    private Response errorResponse(Response.Status status, String message) {
        return Response.status(status).entity(Map.of("error", message)).build();
    }
}
