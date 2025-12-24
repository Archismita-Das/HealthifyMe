package com.healthifyme.healthifyme_backend.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Meal Entity - Represents a logged meal in the database
 *
 * FILE LOCATION: springboot-backend/src/main/java/com/healthifyme/model/Meal.java
 *
 * HOW TO CREATE:
 * Right-click 'model' package → New → Java Class → Name: "Meal"
 *
 * PURPOSE:
 * Tracks user's daily food intake for calorie counting
 * Maps to 'meals' table in MySQL database
 *
 * @author HealthifyMe Team
 */
@Entity
@Table(name = "meals")
public class Meal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Foreign key to users table
     * Links meal to specific user
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * Optional foreign key to foods table
     * Null if custom food entry
     */
    @Column(name = "food_id")
    private Long foodId;

    /**
     * Name of the food consumed
     */
    @Column(name = "food_name", nullable = false)
    private String foodName;

    /**
     * Calories for this meal
     */
    @Column(nullable = false)
    private Integer calories;

    /**
     * Macronutrients (optional)
     */
    private Double protein;
    private Double carbs;
    private Double fats;

    /**
     * Meal type: breakfast, lunch, dinner, snack
     */
    @Column(name = "meal_type", nullable = false)
    private String mealType;

    /**
     * Date of the meal
     */
    @Column(nullable = false)
    private LocalDate date;

    /**
     * Quantity/serving size
     */
    @Column(nullable = false)
    private Double quantity = 1.0;

    /**
     * Timestamp when meal was logged
     */
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // ==================== CONSTRUCTORS ====================

    public Meal() {
    }

    public Meal(Long userId, String foodName, Integer calories, String mealType, LocalDate date) {
        this.userId = userId;
        this.foodName = foodName;
        this.calories = calories;
        this.mealType = mealType;
        this.date = date;
        this.quantity = 1.0;
    }

    public Meal(Long userId, Long foodId, String foodName, Integer calories,
                Double protein, Double carbs, Double fats, String mealType,
                LocalDate date, Double quantity) {
        this.userId = userId;
        this.foodId = foodId;
        this.foodName = foodName;
        this.calories = calories;
        this.protein = protein;
        this.carbs = carbs;
        this.fats = fats;
        this.mealType = mealType;
        this.date = date;
        this.quantity = quantity;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.quantity == null) {
            this.quantity = 1.0;
        }
    }

    // ==================== GETTERS AND SETTERS ====================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getFoodId() {
        return foodId;
    }

    public void setFoodId(Long foodId) {
        this.foodId = foodId;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public Integer getCalories() {
        return calories;
    }

    public void setCalories(Integer calories) {
        this.calories = calories;
    }

    public Double getProtein() {
        return protein;
    }

    public void setProtein(Double protein) {
        this.protein = protein;
    }

    public Double getCarbs() {
        return carbs;
    }

    public void setCarbs(Double carbs) {
        this.carbs = carbs;
    }

    public Double getFats() {
        return fats;
    }

    public void setFats(Double fats) {
        this.fats = fats;
    }

    public String getMealType() {
        return mealType;
    }

    public void setMealType(String mealType) {
        this.mealType = mealType;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Meal{" +
                "id=" + id +
                ", userId=" + userId +
                ", foodName='" + foodName + '\'' +
                ", calories=" + calories +
                ", mealType='" + mealType + '\'' +
                ", date=" + date +
                '}';
    }
}

/**
 * USAGE EXAMPLES:
 *
 * 1. LOG MEAL:
 *    Meal meal = new Meal(userId, "Banana", 105, "breakfast", LocalDate.now());
 *    mealRepository.save(meal);
 *
 * 2. GET TODAY'S MEALS:
 *    List<Meal> todayMeals = mealRepository.findByUserIdAndDate(userId, LocalDate.now());
 *
 * 3. CALCULATE DAILY CALORIES:
 *    Integer total = mealRepository.findByUserIdAndDate(userId, LocalDate.now())
 *        .stream()
 *        .mapToInt(Meal::getCalories)
 *        .sum();
 */
