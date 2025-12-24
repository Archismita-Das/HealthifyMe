package com.healthifyme.healthifyme_backend.controller;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.util.HashMap;
import java.util.Map;

/**
 * Chat Controller - Bridge between Frontend and Flask Chatbot
 *
 * FILE LOCATION: src/main/java/com/healthifyme/controller/ChatController.java
 *
 * PURPOSE:
 * Acts as a middleware that receives chat messages from the frontend,
 * forwards them to the Python Flask chatbot microservice,
 * and returns the chatbot's response back to the frontend.
 *
 * WHY A BRIDGE?
 * 1. Security: Frontend doesn't directly access Flask (can add auth here)
 * 2. Error Handling: Provides friendly error messages if Flask is down
 * 3. Scalability: Can add caching, logging, rate limiting here
 * 4. Flexibility: Easy to switch chatbot backend without changing frontend
 *
 * ENDPOINTS:
 * POST /api/chat - Forward message to Flask chatbot
 * GET /api/test - Simple test endpoint to verify backend is running
 *
 * @author HealthifyMe Team
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") // Allow requests from any origin (frontend)
public class ChatController {

    // Flask chatbot URL - change if Flask runs on different port
    private static final String FLASK_CHATBOT_URL = "http://localhost:5000/chat";

    // RestTemplate for making HTTP requests to Flask
    private final RestTemplate restTemplate;

    /**
     * Constructor - initializes RestTemplate
     * RestTemplate is Spring's HTTP client for calling other services
     */
    public ChatController() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * Test Endpoint
     *
     * Simple endpoint to verify Spring Boot is running.
     *
     * TEST IN BROWSER: http://localhost:8081/api/test
     * TEST WITH CURL: curl http://localhost:8081/api/test
     *
     * @return Welcome message with status
     */
    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> testEndpoint() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "HealthifyMe Backend is running!");
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }

    /**
     * Chat Endpoint - Main chatbot bridge
     *
     * Receives user message from frontend, forwards to Flask, returns response.
     *
     * REQUEST FORMAT (JSON):
     * {
     *   "message": "How many calories in banana?"
     * }
     *
     * RESPONSE FORMAT (JSON):
     * {
     *   "intent": "food_query",
     *   "entities": ["banana"],
     *   "reply": "A medium banana has 105 calories..."
     * }
     *
     * TEST WITH CURL:
     * curl -X POST http://localhost:8081/api/chat \
     *   -H "Content-Type: application/json" \
     *   -d '{"message":"hello"}'
     *
     * @param request Map containing "message" field
     * @return Chatbot response or error message
     */
    @PostMapping("/chat")
    public ResponseEntity<Map<String, Object>> chat(@RequestBody Map<String, String> request) {

        // Extract message from request
        String userMessage = request.get("message");

        // Validate input - ensure message is not empty
        if (userMessage == null || userMessage.trim().isEmpty()) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Message cannot be empty");
            errorResponse.put("reply", "Please enter a message to chat with me!");
            return ResponseEntity.badRequest().body(errorResponse);
        }

        try {
            // Prepare request body for Flask
            Map<String, String> flaskRequest = new HashMap<>();
            flaskRequest.put("message", userMessage);

            // Set headers for Flask request
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Create HTTP entity with body and headers
            HttpEntity<Map<String, String>> entity = new HttpEntity<>(flaskRequest, headers);

            // Forward request to Flask chatbot and get response
            // ResponseEntity captures the full HTTP response including status and body
            ResponseEntity<Map> flaskResponse = restTemplate.exchange(
                    FLASK_CHATBOT_URL,           // Flask endpoint URL
                    HttpMethod.POST,              // HTTP method
                    entity,                       // Request body + headers
                    Map.class                     // Expected response type
            );

            // Extract response body from Flask
            Map<String, Object> responseBody = flaskResponse.getBody();

            // Return Flask response to frontend with HTTP 200 OK
            return ResponseEntity.ok(responseBody);

        } catch (HttpClientErrorException e) {
            // Flask returned 4xx error (bad request, not found, etc.)
            System.err.println("❌ Flask returned error: " + e.getStatusCode());
            System.err.println("Response: " + e.getResponseBodyAsString());

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Chatbot error");
            errorResponse.put("reply", "Sorry, I encountered an error processing your request. Please try again.");
            errorResponse.put("details", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);

        } catch (ResourceAccessException e) {
            // Flask is not running or unreachable
            System.err.println("❌ Cannot connect to Flask chatbot at " + FLASK_CHATBOT_URL);
            System.err.println("Error: " + e.getMessage());
            System.err.println("\n⚠️  SOLUTION: Make sure Flask chatbot is running:");
            System.err.println("   cd flask-chatbot");
            System.err.println("   venv\\Scripts\\activate  (Windows)");
            System.err.println("   python chatbot_api.py\n");

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Chatbot service unavailable");
            errorResponse.put("reply", "The chatbot service is currently offline. Please make sure the Flask chatbot is running on port 5000.");
            errorResponse.put("details", "Connection refused to " + FLASK_CHATBOT_URL);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(errorResponse);

        } catch (Exception e) {
            // Any other unexpected error
            System.err.println("❌ Unexpected error in chat endpoint: " + e.getMessage());
            e.printStackTrace();

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Internal server error");
            errorResponse.put("reply", "An unexpected error occurred. Please try again later.");
            errorResponse.put("details", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Health Check Endpoint for Flask Chatbot
     *
     * Checks if Flask chatbot is reachable.
     * Useful for debugging connectivity issues.
     *
     * TEST: http://localhost:8081/api/chat/health
     *
     * @return Status of Flask chatbot
     */
    @GetMapping("/chat/health")
    public ResponseEntity<Map<String, Object>> checkFlaskHealth() {
        Map<String, Object> response = new HashMap<>();

        try {
            // Try to connect to Flask
            restTemplate.getForObject(FLASK_CHATBOT_URL.replace("/chat", "/"), String.class);
            response.put("flask_status", "online");
            response.put("flask_url", FLASK_CHATBOT_URL);
            response.put("message", "Flask chatbot is reachable");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("flask_status", "offline");
            response.put("flask_url", FLASK_CHATBOT_URL);
            response.put("message", "Flask chatbot is NOT reachable");
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
        }
    }
}
