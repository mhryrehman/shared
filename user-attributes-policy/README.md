# User Attribute SPI Deployment & Policy Configuration in Keycloak

This guide explains how to **deploy the User Attribute SPI** in Keycloak and **add a user attribute policy** using the Keycloak Admin API.

---

## **🚀 Deployment Steps**
### **1️⃣ Build and Deploy the SPI in Keycloak**
#### **Step 1: Build the SPI JAR**
If your **User Attribute SPI** is a Maven project, build it using:
```sh
mvn clean install
This will generate a JAR file inside the target/ directory.

Step 2: Copy the JAR to the Keycloak Server
If Keycloak is running in Docker, copy the JAR:

docker cp ./target/user-attributes-policy-0.0.1.jar keycloak:/opt/bitnami/keycloak/providers
If Keycloak is running locally, move the JAR to Keycloak’s provider directory:

cp ./target/user-attributes-policy-0.0.1.jar /opt/keycloak/providers/
Step 3: Restart Keycloak

docker restart keycloak
Or if running locally:


./kc.sh start-dev

✅ Now, the SPI is deployed.
🔑 Obtain an Admin Client Token
To interact with Keycloak’s API, obtain an admin token using client_credentials:

curl -X POST "http://172.16.102.238:8082/realms/testing/protocol/openid-connect/token" \
     -H "Content-Type: application/x-www-form-urlencoded" \
     -d "client_id=test" \
     -d "client_secret=90tin6VgPryWJViq6h3OGmBD508p60mU" \
     -d "grant_type=client_credentials"
Response Example:
json
{
    "access_token": "eyJhbGciOiJIUzI1...",
    "expires_in": 300,
    "token_type": "Bearer"
}

✅ Copy the "access_token" and use it in the next API calls.
📌 Get the Client ID for the Policy
To obtain the Client ID of the test client:

curl -X GET "http://172.16.102.238:8082/admin/realms/testing/clients?clientId=test" \
     -H "Authorization: Bearer {ACCESS_TOKEN}"
Response Example:
[
    {
        "id": "77eca52c-5e7e-41d9-8581-dc098c022c04",
        "clientId": "test"
    }
]

✅ Extract the "id" (77eca52c-5e7e-41d9-8581-dc098c022c04) and use it in the policy.

➕ Add User Attribute Policy
Once you have the Client ID, use it to create a User Attribute Policy:

curl -X POST "http://172.16.102.238:8082/admin/realms/testing/clients/77eca52c-5e7e-41d9-8581-dc098c022c04/authz/resource-server/policy/user-attribute" \
     -H "Authorization: Bearer {ACCESS_TOKEN}" \
     -H "Content-Type: application/json" \
     -d '{
           "name": "user attribute policy",
           "type": "user-attribute",  //fixed value, you can not change this.
           "description": "Custom regex policy",
           "allowedAttributes": "test-value, custom-value", //A comma-separated list of values (test-value, custom-value) that the user must have in their attributes.
           "userAttributeKey": "customField", //The key (customField) in the user's attributes that will be checked against allowedAttributes.
           "matchAll": "true" // A boolean (true or false) that determines if all values in allowedAttributes must match (true), or if at least one match is sufficient (false).
        }'


✅ Now, the policy is added to Keycloak. Newly created policy will be visible under client>client details>policies. Now create permission using this policy and evalute it.

✅ 🚀 Note
Policy will only be configured using REST Apis. No UI available to configure user attributes policy.

