package com.healthifyme.healthifyme_backend.repository;

import com.healthifyme.healthifyme_backend.model.Food;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Food Repository - Database access for Food entity
 *
 * FILE LOCATION: springboot-backend/src/main/java/com/healthifyme/repository/FoodRepository.java
 *
 * HOW TO CREATE:
 * 1. Right-click 'repository' package → New → Java Class → Name: "FoodRepository"
 * 2. Change "class" to "interface" and extend JpaRepository
 * 3. Paste this code
 *
 * WHAT THIS DOES:
 * Provides database query methods for Food entity
 * Spring Data JPA automatically implements these methods
 *
 * @author HealthifyMe Team
 */
@Repository
public interface FoodRepository extends JpaRepository<Food, Long> {

    /**
     * SEARCH BY NAME (CASE-INSENSITIVE, PARTIAL MATCH)
     * Query: SELECT * FROM foods WHERE LOWER(food_name) LIKE LOWER('%searchTerm%')
     *
     * @param name Search term
     * @return List of matching foods
     *
     * EXAMPLES:
     * findByFoodNameContainingIgnoreCase("paneer") → All paneer dishes
     * findByFoodNameContainingIgnoreCase("CHICKEN") → All chicken items (case ignored)
     * findByFoodNameContainingIgnoreCase("ban") → Banana, etc.
     */
    List<Food> findByFoodNameContainingIgnoreCase(String name);

    /**
     * FIND BY CUISINE
     * Query: SELECT * FROM foods WHERE cuisine = ?
     *
     * @param cuisine Cuisine type (Indian, Global, Common)
     * @return List of foods from that cuisine
     *
     * EXAMPLES:
     * findByCuisine("Indian") → All Indian foods
     * findByCuisine("Global") → All Global foods
     */
    List<Food> findByCuisine(String cuisine);

    /**
     * FIND BY DIET CATEGORY
     * Query: SELECT * FROM foods WHERE diet_category = ?
     *
     * @param dietCategory Diet type (vegan, vegetarian, non-veg)
     * @return List of foods in that diet category
     *
     * EXAMPLES:
     * findByDietCategory("vegan") → All vegan foods
     * findByDietCategory("vegetarian") → All vegetarian foods
     */
    List<Food> findByDietCategory(String dietCategory);

    /**
     * FIND BY FOOD TYPE
     * Query: SELECT * FROM foods WHERE type = ?
     *
     * @param type Food type (protein, carb, fat, vegetable, fruit, dairy)
     * @return List of foods of that type
     *
     * EXAMPLES:
     * findByType("protein") → All protein foods
     * findByType("carb") → All carbohydrate foods
     */
    List<Food> findByType(String type);

    /**
     * FIND LOW CALORIE FOODS
     * Query: SELECT * FROM foods WHERE calories < ?
     *
     * @param calories Maximum calorie limit
     * @return List of foods below calorie limit
     *
     * EXAMPLES:
     * findByCaloriesLessThan(100) → Foods with less than 100 calories
     * findByCaloriesLessThan(200) → Foods with less than 200 calories
     */
    List<Food> findByCaloriesLessThan(Integer calories);

    /**
     * FIND BY CUISINE AND DIET CATEGORY
     * Query: SELECT * FROM foods WHERE cuisine = ? AND diet_category = ?
     *
     * @param cuisine Cuisine type
     * @param dietCategory Diet category
     * @return List of matching foods
     *
     * EXAMPLE:
     * findByCuisineAndDietCategory("Indian", "vegan") → Indian vegan foods
     */
    List<Food> findByCuisineAndDietCategory(String cuisine, String dietCategory);

    /**
     * COMPLEX FILTER: CUISINE + DIET + MAX CALORIES
     * Query: SELECT * FROM foods WHERE cuisine = ? AND diet_category = ? AND calories < ?
     *
     * @param cuisine Cuisine type
     * @param dietCategory Diet category
     * @param maxCalories Maximum calories
     * @return List of matching foods
     *
     * EXAMPLE:
     * findByCuisineAndDietCategoryAndCaloriesLessThan("Indian", "vegan", 200)
     * → Indian vegan foods with less than 200 calories
     */
    List<Food> findByCuisineAndDietCategoryAndCaloriesLessThan(
            String cuisine, String dietCategory, Integer maxCalories);

    /**
     * FIND HIGH PROTEIN FOODS
     * Query: SELECT * FROM foods WHERE protein > ?
     *
     * @param protein Minimum protein in grams
     * @return List of high protein foods
     *
     * EXAMPLE:
     * findByProteinGreaterThan(20.0) → Foods with more than 20g protein
     */
    List<Food> findByProteinGreaterThan(Double protein);

    /**
     * FIND LOW CARB FOODS
     * Query: SELECT * FROM foods WHERE carbs < ?
     *
     * @param carbs Maximum carbs in grams
     * @return List of low carb foods
     *
     * EXAMPLE:
     * findByCarbsLessThan(10.0) → Foods with less than 10g carbs
     */
    List<Food> findByCarbsLessThan(Double carbs);

    /**
     * FIND BY CUISINE AND TYPE
     * Query: SELECT * FROM foods WHERE cuisine = ? AND type = ?
     *
     * @param cuisine Cuisine type
     * @param type Food type
     * @return List of matching foods
     *
     * EXAMPLE:
     * findByCuisineAndType("Indian", "protein") → Indian protein sources
     */
    List<Food> findByCuisineAndType(String cuisine, String type);
}

/**
 * TESTING IN APPLICATION:
 *
 * After creating this repository, test it in your main application:
 *
 * @Autowired
 * private FoodRepository foodRepository;
 *
 * // Test in a method or CommandLineRunner
 * public void testRepository() {
 *     // Search by name
 *     List<Food> paneerDishes = foodRepository.findByFoodNameContainingIgnoreCase("paneer");
 *     System.out.println("Paneer dishes: " + paneerDishes.size());
 *
 *     // Get Indian foods
 *     List<Food> indianFoods = foodRepository.findByCuisine("Indian");
 *     System.out.println("Indian foods: " + indianFoods.size());
 *
 *     // Get vegan foods
 *     List<Food> veganFoods = foodRepository.findByDietCategory("vegan");
 *     System.out.println("Vegan foods: " + veganFoods.size());
 *
 *     // Get low calorie foods
 *     List<Food> lowCal = foodRepository.findByCaloriesLessThan(100);
 *     System.out.println("Low calorie foods: " + lowCal.size());
 * }
 *
 * NOTES:
 * - Spring Data JPA automatically implements these methods
 * - No need to write SQL - JPA generates it from method names
 * - Method naming convention: findBy + FieldName + Operator
 * - Common operators: Containing, IgnoreCase, LessThan, GreaterThan, Between
 */
