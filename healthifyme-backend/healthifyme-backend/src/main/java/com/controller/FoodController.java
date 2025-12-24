package com.healthifyme.healthifyme_backend.controller;

import com.healthifyme.healthifyme_backend.model.Food;
import com.healthifyme.healthifyme_backend.repository.FoodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Food Controller - REST API endpoints for food operations
 *
 * FILE LOCATION: springboot-backend/src/main/java/com/healthifyme/controller/FoodController.java
 *
 * HOW TO CREATE:
 * 1. Right-click 'com.healthifyme' → New → Package → Name: "controller"
 * 2. Right-click 'controller' → New → Java Class → Name: "FoodController"
 * 3. Paste this code
 *
 * WHAT THIS DOES:
 * Provides REST API endpoints for frontend to search and retrieve food data
 *
 * ENDPOINTS PROVIDED:
 * GET  /api/foods              - Get all foods
 * GET  /api/foods/{id}         - Get food by ID
 * GET  /api/foods/search       - Search foods by name
 * GET  /api/foods/cuisine/{cuisine} - Get foods by cuisine
 * GET  /api/foods/filter       - Filter foods by multiple criteria
 *
 * BASE URL: http://localhost:8081/api/foods
 *
 * @author HealthifyMe Team
 */
@RestController  // Marks this as a REST controller
@RequestMapping("/api/foods")  // Base path for all endpoints
@CrossOrigin(origins = "*")  // Allow requests from any origin
public class FoodController {

    /**
     * Automatic dependency injection
     * Spring automatically creates and injects FoodRepository instance
     */
    @Autowired
    private FoodRepository foodRepository;

    // ==================== ENDPOINTS ====================

    /**
     * GET ALL FOODS
     *
     * ENDPOINT: GET /api/foods
     * URL: http://localhost:8081/api/foods
     *
     * RETURNS: All 190 foods from database
     *
     * TEST IN BROWSER:
     * http://localhost:8081/api/foods
     *
     * TEST WITH CURL:
     * curl http://localhost:8081/api/foods
     *
     * RESPONSE FORMAT:
     * [
     *   {
     *     "id": 1,
     *     "foodName": "Roti (Whole Wheat)",
     *     "calories": 71,
     *     "protein": 3.1,
     *     "carbs": 15.0,
     *     "fats": 0.4,
     *     "type": "carb",
     *     "cuisine": "Indian",
     *     "dietCategory": "vegan"
     *   },
     *   ...
     * ]
     *
     * @return List of all foods
     */
    @GetMapping
    public ResponseEntity<List<Food>> getAllFoods() {
        List<Food> foods = foodRepository.findAll();
        return ResponseEntity.ok(foods);
    }

    /**
     * SEARCH FOODS BY NAME
     *
     * ENDPOINT: GET /api/foods/search?name={searchTerm}
     * URL: http://localhost:8081/api/foods/search?name=paneer
     *
     * PARAMETERS:
     * - name: Food name to search (case-insensitive, partial match)
     *
     * EXAMPLES:
     * /api/foods/search?name=paneer  → Returns all paneer dishes
     * /api/foods/search?name=chicken → Returns all chicken items
     * /api/foods/search?name=banana  → Returns banana
     *
     * TEST WITH CURL:
     * curl "http://localhost:8081/api/foods/search?name=paneer"
     *
     * USED BY: frontend/food-search.html
     *
     * @param name Search term
     * @return List of matching foods
     */
    @GetMapping("/search")
    public ResponseEntity<List<Food>> searchFoods(@RequestParam String name) {
        // Validate input
        if (name == null || name.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        // Search database (case-insensitive, partial match)
        List<Food> foods = foodRepository.findByFoodNameContainingIgnoreCase(name);

        // Return results
        return ResponseEntity.ok(foods);
    }

    /**
     * GET FOOD BY ID
     *
     * ENDPOINT: GET /api/foods/{id}
     * URL: http://localhost:8081/api/foods/1
     *
     * PARAMETERS:
     * - id: Food ID (path variable)
     *
     * TEST WITH CURL:
     * curl http://localhost:8081/api/foods/1
     *
     * @param id Food ID
     * @return Food object or 404 if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Food> getFoodById(@PathVariable Long id) {
        return foodRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * GET FOODS BY CUISINE
     *
     * ENDPOINT: GET /api/foods/cuisine/{cuisine}
     * URL: http://localhost:8081/api/foods/cuisine/Indian
     *
     * PARAMETERS:
     * - cuisine: Cuisine type (Indian, Global, Common)
     *
     * EXAMPLES:
     * /api/foods/cuisine/Indian  → All Indian foods
     * /api/foods/cuisine/Global  → All Global foods
     *
     * @param cuisine Cuisine type
     * @return List of foods from that cuisine
     */
    @GetMapping("/cuisine/{cuisine}")
    public ResponseEntity<List<Food>> getFoodsByCuisine(@PathVariable String cuisine) {
        List<Food> foods = foodRepository.findByCuisine(cuisine);
        return ResponseEntity.ok(foods);
    }

    /**
     * FILTER FOODS BY MULTIPLE CRITERIA
     *
     * ENDPOINT: GET /api/foods/filter?cuisine=Indian&diet=vegan&maxCalories=200
     * URL: http://localhost:8081/api/foods/filter?cuisine=Indian&diet=vegan&maxCalories=200
     *
     * PARAMETERS (all optional):
     * - cuisine: Cuisine type (Indian, Global, Common)
     * - diet: Diet category (vegan, vegetarian, non-veg)
     * - maxCalories: Maximum calories
     * - type: Food type (protein, carb, fat, etc.)
     *
     * EXAMPLES:
     * /api/foods/filter?diet=vegan
     * /api/foods/filter?cuisine=Indian&diet=vegan
     * /api/foods/filter?maxCalories=100
     * /api/foods/filter?cuisine=Indian&diet=vegan&maxCalories=200
     *
     * TEST WITH CURL:
     * curl "http://localhost:8081/api/foods/filter?cuisine=Indian&diet=vegan&maxCalories=200"
     *
     * @param cuisine Optional cuisine filter
     * @param diet Optional diet category filter
     * @param maxCalories Optional calorie limit
     * @param type Optional food type filter
     * @return List of matching foods
     */
    @GetMapping("/filter")
    public ResponseEntity<List<Food>> filterFoods(
            @RequestParam(required = false) String cuisine,
            @RequestParam(required = false) String diet,
            @RequestParam(required = false) Integer maxCalories,
            @RequestParam(required = false) String type) {

        List<Food> foods;

        // Apply filters based on provided parameters
        if (cuisine != null && diet != null && maxCalories != null) {
            // All three filters
            foods = foodRepository.findByCuisineAndDietCategoryAndCaloriesLessThan(
                    cuisine, diet, maxCalories);
        } else if (diet != null) {
            // Diet filter only
            foods = foodRepository.findByDietCategory(diet);
        } else if (cuisine != null) {
            // Cuisine filter only
            foods = foodRepository.findByCuisine(cuisine);
        } else if (type != null) {
            // Type filter only
            foods = foodRepository.findByType(type);
        } else if (maxCalories != null) {
            // Calorie filter only
            foods = foodRepository.findByCaloriesLessThan(maxCalories);
        } else {
            // No filters - return all
            foods = foodRepository.findAll();
        }

        return ResponseEntity.ok(foods);
    }

    /**
     * GET STATISTICS
     *
     * ENDPOINT: GET /api/foods/stats
     * URL: http://localhost:8081/api/foods/stats
     *
     * RETURNS: Database statistics
     *
     * RESPONSE:
     * {
     *   "totalFoods": 190,
     *   "indianFoods": 70,
     *   "globalFoods": 70,
     *   "veganFoods": 150
     * }
     *
     * @return Statistics map
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getFoodStats() {
        Map<String, Object> stats = new HashMap<>();

        stats.put("totalFoods", foodRepository.count());
        stats.put("indianFoods", foodRepository.findByCuisine("Indian").size());
        stats.put("globalFoods", foodRepository.findByCuisine("Global").size());
        stats.put("veganFoods", foodRepository.findByDietCategory("vegan").size());
        stats.put("vegetarianFoods", foodRepository.findByDietCategory("vegetarian").size());

        return ResponseEntity.ok(stats);
    }
}

/**
 * TESTING GUIDE:
 *
 * 1. START APPLICATION:
 *    Run HealthifyMeApplication.java
 *    Should see: "✅ HealthifyMe Backend Started Successfully!"
 *
 * 2. TEST IN BROWSER:
 *    http://localhost:8081/api/foods
 *    Should see JSON array of all foods
 *
 * 3. TEST SEARCH:
 *    http://localhost:8081/api/foods/search?name=paneer
 *    Should return all paneer dishes
 *
 * 4. TEST WITH POSTMAN:
 *    GET http://localhost:8081/api/foods/search?name=banana
 *
 * 5. TEST WITH CURL:
 *    curl http://localhost:8081/api/foods/search?name=chicken
 *
 * 6. TEST FROM FRONTEND:
 *    Open food-search.html
 *    Type "paneer" and click Search
 *    Should display results
 *
 * TROUBLESHOOTING:
 *
 * Problem: "404 Not Found"
 * Solution: Check URL includes /api/foods
 *          Verify controller has @RestController and @RequestMapping
 *
 * Problem: Empty results
 * Solution: Check database has data: SELECT COUNT(*) FROM foods;
 *          Verify MySQL is running
 *
 * Problem: CORS errors
 * Solution: Already handled with @CrossOrigin(origins = "*")
 *          If issues persist, check browser console
 *
 * INTEGRATION WITH FRONTEND:
 *
 * frontend/food-search.html uses this endpoint:
 *
 * async function searchFood() {
 *     const query = document.getElementById('searchInput').value;
 *     const response = await fetch(
 *         `http://localhost:8081/api/foods/search?name=${query}`
 *     );
 *     const foods = await response.json();
 *     // Display foods...
 * }
 */