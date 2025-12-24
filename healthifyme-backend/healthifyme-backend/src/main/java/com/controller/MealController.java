package com.healthifyme.healthifyme_backend.controller;

import com.healthifyme.healthifyme_backend.model.Meal;
import com.healthifyme.healthifyme_backend.repository.MealRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Meal Controller - API endpoints for meal logging and tracking
 *
 * FILE LOCATION: springboot-backend/src/main/java/com/healthifyme/controller/MealController.java
 *
 * HOW TO CREATE:
 * Right-click 'controller' package → New → Java Class → Name: "MealController"
 *
 * ENDPOINTS:
 * POST   /api/meals          - Log new meal
 * GET    /api/meals/user/{userId}  - Get all meals for user
 * GET    /api/meals/today    - Get today's meals
 * GET    /api/meals/date     - Get meals for specific date
 * GET    /api/meals/range    - Get meals for date range
 * GET    /api/meals/summary  - Get daily summary (calories, protein, count)
 * DELETE /api/meals/{id}     - Delete a meal
 *
 * @author HealthifyMe Team
 */
@RestController
@RequestMapping("/api/meals")
@CrossOrigin(origins = "*")
public class MealController {

    @Autowired
    private MealRepository mealRepository;

    // ==================== LOG NEW MEAL ====================

    /**
     * LOG MEAL - Create new meal entry
     * <p>
     * ENDPOINT: POST /api/meals
     * URL: http://localhost:8081/api/meals
     * <p>
     * REQUEST BODY (JSON):
     * {
     * "userId": 1,
     * "foodName": "Banana",
     * "calories": 105,
     * "protein": 1.3,
     * "carbs": 27.0,
     * "fats": 0.4,
     * "mealType": "breakfast",
     * "date": "2025-01-20",
     * "quantity": 1.0
     * }
     * <p>
     * RESPONSE (201 Created):
     * {
     * "success": true,
     * "message": "Meal logged successfully",
     * "meal": { ...meal object... }
     * }
     * <p>
     * TEST WITH CURL:
     * curl -X POST http://localhost:8081/api/meals \
     * -H "Content-Type: application/json" \
     * -d '{
     * "userId":1,
     * "foodName":"Banana",
     * "calories":105,
     * "protein":1.3,
     * "mealType":"breakfast",
     * "date":"2025-01-20"
     * }'
     *
     * @param meal Meal object from request body
     * @return Response with saved meal
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> logMeal(@RequestBody Meal meal) {
        Map<String, Object> response = new HashMap<>();

        // Validation
        if (meal.getUserId() == null) {
            response.put("success", false);
            response.put("message", "User ID is required");
            return ResponseEntity.badRequest().body(response);
        }

        if (meal.getFoodName() == null || meal.getFoodName().trim().isEmpty()) {
            response.put("success", false);
            response.put("message", "Food name is required");
            return ResponseEntity.badRequest().body(response);
        }

        if (meal.getCalories() == null || meal.getCalories() <= 0) {
            response.put("success", false);
            response.put("message", "Valid calories are required");
            return ResponseEntity.badRequest().body(response);
        }

        if (meal.getMealType() == null || meal.getMealType().trim().isEmpty()) {
            response.put("success", false);
            response.put("message", "Meal type is required");
            return ResponseEntity.badRequest().body(response);
        }

        // Set date to today if not provided
        if (meal.getDate() == null) {
            meal.setDate(LocalDate.now());
        }

        try {
            // Save meal
            Meal savedMeal = mealRepository.save(meal);

            // Success response
            response.put("success", true);
            response.put("message", "Meal logged successfully");
            response.put("meal", savedMeal);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to log meal: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ==================== GET MEALS ====================

    /**
     * GET ALL MEALS FOR USER
     * <p>
     * ENDPOINT: GET /api/meals/user/{userId}
     * URL: http://localhost:8081/api/meals/user/1
     *
     * @param userId User ID
     * @return List of all user's meals
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Meal>> getUserMeals(@PathVariable Long userId) {
        List<Meal> meals = mealRepository.findByUserIdOrderByDateDescCreatedAtDesc(userId);
        return ResponseEntity.ok(meals);
    }

    /**
     * GET TODAY'S MEALS
     * <p>
     * ENDPOINT: GET /api/meals/today?userId=1
     * URL: http://localhost:8081/api/meals/today?userId=1
     *
     * @param userId User ID
     * @return List of today's meals
     */
    @GetMapping("/today")
    public ResponseEntity<List<Meal>> getTodayMeals(@RequestParam Long userId) {
        List<Meal> meals = mealRepository.findByUserIdAndDate(userId, LocalDate.now());
        return ResponseEntity.ok(meals);
    }

    /**
     * GET MEALS FOR SPECIFIC DATE
     * <p>
     * ENDPOINT: GET /api/meals/date?userId=1&date=2025-01-20
     * URL: http://localhost:8081/api/meals/date?userId=1&date=2025-01-20
     *
     * @param userId User ID
     * @param date   Date in format yyyy-MM-dd
     * @return List of meals for that date
     */
    @GetMapping("/date")
    public ResponseEntity<List<Meal>> getMealsByDate(
            @RequestParam Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<Meal> meals = mealRepository.findByUserIdAndDate(userId, date);
        return ResponseEntity.ok(meals);
    }

}