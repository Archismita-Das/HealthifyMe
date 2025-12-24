package com.healthifyme.healthifyme_backend;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * HealthifyMe Spring Boot Application - Main Entry Point
 *
 * FILE LOCATION: springboot-backend/src/main/java/com/healthifyme/HealthifyMeApplication.java
 *
 * HOW TO CREATE THIS FILE IN INTELLIJ:
 * 1. Open IntelliJ IDEA
 * 2. File → New → Project
 * 3. Select "Spring Initializr"
 * 4. Group: com.healthifyme
 * 5. Artifact: healthifyme-backend
 * 6. Java: 17 (or 11)
 * 7. Dependencies: Spring Web, Spring Data JPA, MySQL Driver
 * 8. Click Finish
 * 9. This file will be auto-created
 * 10. Replace its content with this code
 *
 * WHAT THIS FILE DOES:
 * - Main entry point for Spring Boot application
 * - Configures CORS to allow frontend (port 5500) to communicate with backend (port 8081)
 * - Starts embedded Tomcat server
 *
 * TO RUN THIS APPLICATION:
 * Method 1 (IntelliJ):
 *   - Right-click this file
 *   - Select "Run 'HealthifyMeApplication'"
 *
 * Method 2 (Terminal):
 *   cd springboot-backend
 *   mvn spring-boot:run
 *
 * Method 3 (Command Prompt):
 *   cd springboot-backend
 *   mvnw spring-boot:run
 *
 * EXPECTED OUTPUT:
 * ========================================
 * ✅ HealthifyMe Backend Started Successfully!
 * 🌐 Server running on: http://localhost:8081
 * 📚 Test endpoint: http://localhost:8081/api/test
 * 💬 Chat endpoint: http://localhost:8081/api/chat
 * ========================================
 *
 * @author HealthifyMe Team
 * @version 1.0
 */
@SpringBootApplication
public class HealthifyMeApplication {

    /**
     * Main method - Application starts here
     *
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        // Start Spring Boot application
        SpringApplication.run(HealthifyMeApplication.class, args);

        // Print startup message
        System.out.println("\n========================================");
        System.out.println("✅ HealthifyMe Backend Started Successfully!");
        System.out.println("🌐 Server running on: http://localhost:8081");
        System.out.println("📚 Test endpoint: http://localhost:8081/api/test");
        System.out.println("💬 Chat endpoint: http://localhost:8081/api/chat");
        System.out.println("🍎 Food search: http://localhost:8081/api/foods/search?name=banana");
        System.out.println("========================================\n");
    }

    /**
     * CORS Configuration Bean
     *
     * WHAT IS CORS?
     * Cross-Origin Resource Sharing - allows frontend (different port/domain)
     * to make requests to this backend.
     *
     * WHY NEEDED?
     * Frontend runs on http://127.0.0.1:5500
     * Backend runs on http://localhost:8081
     * Browser blocks cross-origin requests by default for security.
     * This configuration allows it.
     *
     * SECURITY NOTE:
     * In production, replace "*" with specific frontend URL:
     * .allowedOrigins("https://yourdomain.com")
     *
     * @return WebMvcConfigurer with CORS settings
     */
    @Bean
    public WebMvcConfigurer CorsConfig() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")  // Apply to all /api/* endpoints
                        .allowedOrigins(
                                "http://127.0.0.1:5500",      // VS Code Live Server
                                "http://localhost:5500",       // Localhost variant
                                "http://localhost:3000",       // React dev server (if used)
                                "*"                            // Allow all (development only)
                        )
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")  // HTTP methods
                        .allowedHeaders("*")               // Allow all headers
                        .allowCredentials(false)           // Set true if using cookies/sessions
                        .maxAge(3600);                     // Cache preflight for 1 hour
            }
        };
    }
}

/**
 * TROUBLESHOOTING GUIDE:
 *
 * Problem: "Port 8081 is already in use"
 * Solution: Change port in application.properties:
 *           server.port=8082
 *           Then update frontend API_CONFIG.SPRING_BOOT to use 8082
 *
 * Problem: "Failed to configure a DataSource"
 * Solution: 1. Check application.properties has correct MySQL password
 *           2. Ensure MySQL is running (open MySQL Workbench)
 *           3. Verify fitness_db database exists
 *
 * Problem: "Cannot find symbol SpringApplication"
 * Solution: 1. Right-click pom.xml → Maven → Reload Project
 *           2. File → Invalidate Caches / Restart
 *           3. Check internet connection (Maven downloads dependencies)
 *
 * Problem: Application starts but APIs don't work
 * Solution: 1. Check console for errors
 *           2. Test: http://localhost:8081/api/test in browser
 *           3. Check all controller files are in correct package
 *
 * Problem: CORS errors in browser
 * Solution: Already handled by corsConfigurer() method above
 *           If still issues, clear browser cache
 *
 * WHAT TO DO AFTER STARTING:
 * 1. Open browser: http://localhost:8081/api/test
 *    Should see: {"status":"success","message":"HealthifyMe Backend is running!"}
 *
 * 2. Test chat endpoint:
 *    curl -X POST http://localhost:8081/api/chat -H "Content-Type: application/json" -d "{\"message\":\"hello\"}"
 *
 * 3. Test food search:
 *    http://localhost:8081/api/foods/search?name=banana
 *
 * If all work → ✅ Backend is ready!
 * If not → Check troubleshooting guide above
 */
