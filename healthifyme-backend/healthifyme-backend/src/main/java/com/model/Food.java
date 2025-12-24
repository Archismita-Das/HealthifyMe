package com.healthifyme.healthifyme_backend.model;

import jakarta.persistence.*;

/**
 * Food Entity - Represents a food item in the database
 *
 * FILE LOCATION: springboot-backend/src/main/java/com/healthifyme/model/Food.java
 *
 * HOW TO CREATE:
 * Right-click 'model' package → New → Java Class → Name: "Food"
 *
 * MAPS TO TABLE: foods (already exists in fitness_db with 190 items)
 *
 * @author HealthifyMe Team
 */
@Entity
@Table(name = "foods")
public class Food {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "food_name", nullable = false)
    private String foodName;

    @Column(nullable = false)
    private Integer calories;

    @Column(nullable = false)
    private String type;  // protein, carb, fat, vegetable, fruit, dairy

    @Column(nullable = false)
    private String cuisine;  // Indian, Global, Common

    @Column(name = "diet_category", nullable = false)
    private String dietCategory;  // vegan, vegetarian, non-veg

    private Double protein;
    private Double carbs;
    private Double fats;

    @Column(columnDefinition = "TEXT")
    private String description;

    // ==================== CONSTRUCTORS ====================

    public Food() {
    }

    public Food(String foodName, Integer calories, String type, String cuisine,
                String dietCategory, Double protein, Double carbs, Double fats, String description) {
        this.foodName = foodName;
        this.calories = calories;
        this.type = type;
        this.cuisine = cuisine;
        this.dietCategory = dietCategory;
        this.protein = protein;
        this.carbs = carbs;
        this.fats = fats;
        this.description = description;
    }

    // ==================== GETTERS AND SETTERS ====================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCuisine() {
        return cuisine;
    }

    public void setCuisine(String cuisine) {
        this.cuisine = cuisine;
    }

    public String getDietCategory() {
        return dietCategory;
    }

    public void setDietCategory(String dietCategory) {
        this.dietCategory = dietCategory;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Food{" +
                "id=" + id +
                ", foodName='" + foodName + '\'' +
                ", calories=" + calories +
                ", protein=" + protein +
                ", carbs=" + carbs +
                ", fats=" + fats +
                ", type='" + type + '\'' +
                ", cuisine='" + cuisine + '\'' +
                ", dietCategory='" + dietCategory + '\'' +
                '}';
    }
}

/**
 * USAGE EXAMPLES:
 *
 * 1. SEARCH BY NAME:
 *    List<Food> foods = foodRepository.findByFoodNameContainingIgnoreCase("paneer");
 *
 * 2. FILTER BY CUISINE:
 *    List<Food> indianFoods = foodRepository.findByCuisine("Indian");
 *
 * 3. GET LOW CALORIE FOODS:
 *    List<Food> lowCal = foodRepository.findByCaloriesLessThan(200);
 */