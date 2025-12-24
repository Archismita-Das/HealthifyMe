package com.healthifyme.healthifyme_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * CORS Configuration - Allows frontend to communicate with backend
 *
 * FILE LOCATION: springboot-backend/src/main/java/com/healthifyme/config/CorsConfig.java
 *
 * HOW TO CREATE:
 * 1. Right-click 'com.healthifyme.healthifyme_backend' → New → Package → Name: "config"
 * 2. Right-click 'config' → New → Java Class → Name: "CorsConfig"
 * 3. Paste this code
 *
 * WHAT THIS DOES:
 * - Enables Cross-Origin Resource Sharing (CORS)
 * - Allows frontend (port 5500) to call backend APIs (port 8081)
 * - Without this, browser will block requests due to CORS policy
 *
 * WHY YOU NEED THIS:
 * When frontend (VS Code Live Server on 5500) tries to call backend (Spring Boot on 8081),
 * browsers block it by default for security. This configuration tells Spring Boot to allow it.
 *
 * @author HealthifyMe Team
 */
@Configuration
public class CorsConfig {

    /**
     * Configure CORS settings
     *
     * This allows:
     * - Frontend running on http://localhost:5500 (VS Code Live Server)
     * - Frontend running on http://127.0.0.1:5500 (alternative localhost)
     * - Any origin during development (for testing)
     *
     * To call backend APIs on http://localhost:8081
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")  // Apply to all /api/* endpoints
                        .allowedOrigins(
                                "http://localhost:5500",      // VS Code Live Server
                                "http://127.0.0.1:5500",      // Alternative localhost
                                "http://localhost:5501",      // Alternative port
                                "http://localhost:5502",      // Alternative port
                                "*"                           // Allow all (for development only)
                        )
                        .allowedMethods(
                                "GET",      // Read data
                                "POST",     // Create data
                                "PUT",      // Update data
                                "DELETE",   // Delete data
                                "OPTIONS"   // Preflight requests
                        )
                        .allowedHeaders("*")    // Allow all headers
                        .allowCredentials(false) // Set to true if using cookies/authentication
                        .maxAge(3600);          // Cache preflight response for 1 hour
            }
        };
    }
}

/**
 * CONFIGURATION EXPLANATION:
 *
 * 1. allowedOrigins:
 *    - Lists which frontend URLs can access the backend
 *    - Your frontend runs on http://localhost:5500 (VS Code Live Server)
 *    - During development, "*" allows any origin
 *
 * 2. allowedMethods:
 *    - GET: Retrieve data (getAllFoods, searchFoods, etc.)
 *    - POST: Create/update data (logWater, addWater, etc.)
 *    - PUT: Update existing data
 *    - DELETE: Delete data (deleteWaterLog)
 *    - OPTIONS: Browser sends this before actual request (preflight)
 *
 * 3. allowedHeaders:
 *    - "*" means accept any headers (Content-Type, Authorization, etc.)
 *
 * 4. allowCredentials:
 *    - false: Don't send cookies/authentication (simple API)
 *    - Set to true if you add login/authentication later
 *
 * 5. maxAge:
 *    - Browser caches CORS response for 1 hour
 *    - Reduces preflight requests
 *
 * ALTERNATIVE: Using @CrossOrigin Annotation
 * ============================================
 * You already have @CrossOrigin(origins = "*") on your controllers,
 * which also works! This CorsConfig provides a centralized approach.
 *
 * You can use EITHER:
 * - CorsConfig (this file) - Global configuration
 * - @CrossOrigin on each controller - Per-controller configuration
 * - Both (they work together)
 *
 * PRODUCTION DEPLOYMENT:
 * ======================
 * Before deploying to production, change:
 *
 * .allowedOrigins("*")  // DON'T USE IN PRODUCTION!
 *
 * To specific domains:
 *
 * .allowedOrigins(
 *     "https://yourdomain.com",
 *     "https://www.yourdomain.com"
 * )
 *
 * TESTING CORS:
 * =============
 * 1. Start backend: Run HealthifyMeApplication.java (port 8081)
 * 2. Start frontend: Open with Live Server (port 5500)
 * 3. Open browser console (F12)
 * 4. Try fetching data:
 *
 *    fetch('http://localhost:8081/api/foods')
 *      .then(res => res.json())
 *      .then(data => console.log(data));
 *
 * 5. Should see data without CORS errors
 *
 * TROUBLESHOOTING:
 * ================
 * Problem: "CORS policy: No 'Access-Control-Allow-Origin' header"
 * Solution:
 *   - Verify CorsConfig is in 'config' package
 *   - Verify @Configuration annotation is present
 *   - Restart Spring Boot application
 *   - Clear browser cache
 *
 * Problem: "Method not allowed"
 * Solution:
 *   - Check allowedMethods includes your HTTP method
 *   - Verify controller has correct @PostMapping, @GetMapping, etc.
 *
 * Problem: Preflight request fails
 * Solution:
 *   - Add "OPTIONS" to allowedMethods (already included)
 *   - Check server logs for errors
 */