package com.healthifyme.healthifyme_backend.repository;

import com.healthifyme.healthifyme_backend.model.Meal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Meal Repository - Database access for Meal entity
 *
 * FILE LOCATION: springboot-backend/src/main/java/com/healthifyme/repository/MealRepository.java
 *
 * HOW TO CREATE:
 * Right-click 'repository' package → New → Java Class → Name: "MealRepository"
 * Change "class" to "interface" and extend JpaRepository
 *
 * @author HealthifyMe Team
 */
@Repository
public interface MealRepository extends JpaRepository<Meal, Long> {

    /**
     * Find all meals for a specific user
     * Query: SELECT * FROM meals WHERE user_id = ?
     *
     * @param userId User ID
     * @return List of user's meals
     */
    List<Meal> findByUserId(Long userId);

    /**
     * Find meals for a specific user on a specific date
     * Query: SELECT * FROM meals WHERE user_id = ? AND date = ?
     *
     * @param userId User ID
     * @param date Date
     * @return List of meals for that day
     *
     * USAGE:
     * List<Meal> todayMeals = mealRepository.findByUserIdAndDate(userId, LocalDate.now());
     */
    List<Meal> findByUserIdAndDate(Long userId, LocalDate date);

    /**
     * Find meals by user and date range
     * Query: SELECT * FROM meals WHERE user_id = ? AND date BETWEEN ? AND ?
     *
     * @param userId User ID
     * @param startDate Start date (inclusive)
     * @param endDate End date (inclusive)
     * @return List of meals in date range
     *
     * USAGE:
     * LocalDate today = LocalDate.now();
     * LocalDate weekAgo = today.minusDays(7);
     * List<Meal> weekMeals = mealRepository.findByUserIdAndDateBetween(userId, weekAgo, today);
     */
    List<Meal> findByUserIdAndDateBetween(Long userId, LocalDate startDate, LocalDate endDate);

    /**
     * Find meals by user and meal type
     * Query: SELECT * FROM meals WHERE user_id = ? AND meal_type = ?
     *
     * @param userId User ID
     * @param mealType Meal type (breakfast, lunch, dinner, snack)
     * @return List of meals of that type
     */
    List<Meal> findByUserIdAndMealType(Long userId, String mealType);

    /**
     * Find meals by user, date, and meal type
     * Query: SELECT * FROM meals WHERE user_id = ? AND date = ? AND meal_type = ?
     *
     * @param userId User ID
     * @param date Date
     * @param mealType Meal type
     * @return List of meals
     */
    List<Meal> findByUserIdAndDateAndMealType(Long userId, LocalDate date, String mealType);

    /**
     * Calculate total calories for a user on a specific date
     * Custom query using @Query annotation
     *
     * @param userId User ID
     * @param date Date
     * @return Total calories (or 0 if no meals)
     *
     * USAGE:
     * Integer totalCal = mealRepository.getTotalCaloriesByUserIdAndDate(userId, LocalDate.now());
     */
    @Query("SELECT COALESCE(SUM(m.calories), 0) FROM Meal m WHERE m.userId = :userId AND m.date = :date")
    Integer getTotalCaloriesByUserIdAndDate(@Param("userId") Long userId, @Param("date") LocalDate date);

    /**
     * Calculate total protein for a user on a specific date
     *
     * @param userId User ID
     * @param date Date
     * @return Total protein in grams
     */
    @Query("SELECT COALESCE(SUM(m.protein), 0) FROM Meal m WHERE m.userId = :userId AND m.date = :date")
    Double getTotalProteinByUserIdAndDate(@Param("userId") Long userId, @Param("date") LocalDate date);

    /**
     * Get meals ordered by date (most recent first)
     * Query: SELECT * FROM meals WHERE user_id = ? ORDER BY date DESC, created_at DESC
     *
     * @param userId User ID
     * @return List of meals ordered by date
     */
    List<Meal> findByUserIdOrderByDateDescCreatedAtDesc(Long userId);

    /**
     * Count meals for a user on a specific date
     * Query: SELECT COUNT(*) FROM meals WHERE user_id = ? AND date = ?
     *
     * @param userId User ID
     * @param date Date
     * @return Number of meals logged
     */
    Long countByUserIdAndDate(Long userId, LocalDate date);

    /**
     * Delete all meals for a user on a specific date
     * Query: DELETE FROM meals WHERE user_id = ? AND date = ?
     *
     * @param userId User ID
     * @param date Date
     */
    void deleteByUserIdAndDate(Long userId, LocalDate date);
}

/**
 * USAGE EXAMPLES IN CONTROLLER:
 *
 * 1. GET TODAY'S MEALS:
 *    @GetMapping("/meals/today")
 *    public List<Meal> getTodayMeals(@RequestParam Long userId) {
 *        return mealRepository.findByUserIdAndDate(userId, LocalDate.now());
 *    }
 *
 * 2. GET TOTAL CALORIES:
 *    Integer calories = mealRepository.getTotalCaloriesByUserIdAndDate(userId, LocalDate.now());
 *
 * 3. LOG NEW MEAL:
 *    Meal meal = new Meal(userId, "Banana", 105, "breakfast", LocalDate.now());
 *    mealRepository.save(meal);
 *
 * 4. GET LAST 7 DAYS:
 *    LocalDate today = LocalDate.now();
 *    LocalDate weekAgo = today.minusDays(7);
 *    List<Meal> weekMeals = mealRepository.findByUserIdAndDateBetween(userId, weekAgo, today);
 */
