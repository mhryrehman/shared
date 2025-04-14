package com.example.externalapi.controller;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class NovuOtpEmailService {

    private static final String NOVU_API_URL = "https://kube.novelhealth.ai:3000/v1/events/trigger";
    private static final String API_KEY = "3645ff7f93c6c2de22957af23f4edf8a"; // Replace with your real API key

    public void sendOtpEmail(String subscriberId, String firstName, String otp, String expirySeconds) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "ApiKey " + API_KEY);

        // Payload body
        Map<String, Object> payload = new HashMap<>();
        payload.put("name", "keycloak-otp-notification"); // Workflow name in Novu

        Map<String, Object> to = new HashMap<>();
        to.put("subscriberId", subscriberId);
        payload.put("to", to);

        Map<String, Object> variables = new HashMap<>();
        variables.put("firstName", firstName);
        variables.put("otp", otp);
        variables.put("expiry", expirySeconds);
        payload.put("payload", variables);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(NOVU_API_URL, entity, String.class);
        System.out.println("Novu API Response: " + response.getBody());
    }
}
