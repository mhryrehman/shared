# Keycloak Phone Verification Authenticator SPI

This custom Keycloak Authenticator SPI adds a verification step in the **Direct Access Grant** (password grant type) flow.  
It ensures that the user has the `PhoneVerified` attribute set to `true` before allowing token issuance.

---

## 🔧 Use Case

Block login using **password grant type** unless the user's phone number has been verified (indicated via a custom attribute `PhoneVerified=true`).

---

## ⚙️ Setup Instructions

1. Build this SPI project using Maven:
    ```bash
    mvn clean package
    ```

2. Copy the generated JAR into your Keycloak `providers/` directory:
    ```bash
    cp target/keycloak-phone-verification-spi.jar $KEYCLOAK_HOME/providers/
    ```

3. Rebuild Keycloak:
    ```bash
    $KEYCLOAK_HOME/bin/kc.sh build
    ```

4. Start Keycloak:
    ```bash
    $KEYCLOAK_HOME/bin/kc.sh start-dev
    ```

5. Go to **Authentication > Flows** and create a new flow based on `Direct Grant` (e.g., `Phone Verified Direct Grant`).

6. Add `Verify Phone Number` as an execution step and set it to `REQUIRED`.

7. In **Clients > [Your Client] > Authentication Flow Overrides**, assign the new flow under **Direct Access Grant Flow**.

---

## ✅ API to Set `PhoneVerified=true`

Use the Keycloak Admin REST API to update the `PhoneVerified` attribute for a user.

### 🔐 Update User

```PUT {{keycloak_url}}/admin/realms/{{realm}}/users/d3d86d33-3b5e-44b2-881b-030cffeef85b
{
    "id": "d3d86d33-3b5e-44b2-881b-030cffeef85b",
    "username": "yasir",
    "firstName": "Mehar",
    "lastName": "Yasir",
    "email": "mhryrehman@gmail.com",
    "emailVerified": true,
    "attributes": {
        "PhoneVerified": [
            "true"
        ]
    }
}'
