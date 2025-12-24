package com.healthifyme.healthifyme_backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * User Entity - Represents a user in the database
 *
 * FILE LOCATION: springboot-backend/src/main/java/com/healthifyme/model/User.java
 *
 * HOW TO CREATE THIS FILE:
 * 1. In IntelliJ, right-click on 'com.healthifyme' package
 * 2. New → Package → Name it "model"
 * 3. Right-click on 'model' package
 * 4. New → Java Class → Name it "User"
 * 5. Paste this code
 *
 * WHAT THIS FILE DOES:
 * - Maps to 'users' table in MySQL database
 * - JPA will auto-create table if it doesn't exist (thanks to ddl-auto=update)
 * - Each instance represents one user row
 *
 * TABLE STRUCTURE IN MYSQL:
 * CREATE TABLE users (
 *     id BIGINT PRIMARY KEY AUTO_INCREMENT,
 *     username VARCHAR(255) NOT NULL,
 *     email VARCHAR(255) UNIQUE NOT NULL,
 *     password VARCHAR(255) NOT NULL,
 *     age INT,
 *     height INT,
 *     weight DOUBLE,
 *     goal VARCHAR(50),
 *     created_at TIMESTAMP
 * );
 *
 * @author HealthifyMe Team
 */
@Entity  // Tells JPA this is a database entity
@Table(name = "users")  // Maps to 'users' table
public class User {

    // ==================== FIELDS ====================

    /**
     * Primary Key - Auto-generated ID
     * @GeneratedValue tells JPA to auto-increment this value
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Username - Required field
     * @Column specifies database column properties
     */
    @Column(nullable = false)
    private String username;

    /**
     * Email - Must be unique across all users
     */
    @Column(nullable = false, unique = true)
    private String email;

    /**
     * Password - Stored as plain text (ONLY FOR DEMO!)
     *
     * ⚠️ SECURITY WARNING:
     * In production, NEVER store plain passwords!
     * Use BCryptPasswordEncoder:
     *
     * import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
     *
     * BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
     * String hashedPassword = encoder.encode(plainPassword);
     */
    @Column(nullable = false)
    private String password;

    /**
     * Age in years
     */
    private Integer age;

    /**
     * Height in centimeters
     */
    private Integer height;

    /**
     * Weight in kilograms
     */
    private Double weight;

    /**
     * Fitness goal: weight_loss, muscle_gain, maintenance, general_fitness
     */
    private String goal;

    /**
     * Account creation timestamp
     * Automatically set when entity is first persisted
     */
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // ==================== CONSTRUCTORS ====================

    /**
     * Default constructor (required by JPA)
     * JPA uses this to create instances when reading from database
     */
    public User() {
    }

    /**
     * Full constructor for creating new users
     *
     * @param username User's full name
     * @param email User's email address
     * @param password User's password (should be hashed in production)
     * @param age User's age
     * @param height User's height in cm
     * @param weight User's weight in kg
     * @param goal User's fitness goal
     */
    public User(String username, String email, String password, Integer age,
                Integer height, Double weight, String goal) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.age = age;
        this.height = height;
        this.weight = weight;
        this.goal = goal;
        this.createdAt = LocalDateTime.now();
    }

    // ==================== LIFECYCLE CALLBACKS ====================

    /**
     * Called automatically before entity is saved to database for first time
     * Sets createdAt timestamp
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // ==================== GETTERS AND SETTERS ====================

    /**
     * Get user ID
     * @return User's unique identifier
     */
    public Long getId() {
        return id;
    }

    /**
     * Set user ID
     * Usually not called manually - JPA handles this
     * @param id User ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Get username
     * @return User's full name
     */
    public String getUsername() {
        return username;
    }

    /**
     * Set username
     * @param username User's full name
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Get email
     * @return User's email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Set email
     * @param email User's email address
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Get password
     * @return User's password (plain text in this demo)
     */
    public String getPassword() {
        return password;
    }

    /**
     * Set password
     * @param password User's password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Get age
     * @return User's age in years
     */
    public Integer getAge() {
        return age;
    }

    /**
     * Set age
     * @param age User's age in years
     */
    public void setAge(Integer age) {
        this.age = age;
    }

    /**
     * Get height
     * @return User's height in centimeters
     */
    public Integer getHeight() {
        return height;
    }

    /**
     * Set height
     * @param height User's height in centimeters
     */
    public void setHeight(Integer height) {
        this.height = height;
    }

    /**
     * Get weight
     * @return User's weight in kilograms
     */
    public Double getWeight() {
        return weight;
    }

    /**
     * Set weight
     * @param weight User's weight in kilograms
     */
    public void setWeight(Double weight) {
        this.weight = weight;
    }

    /**
     * Get fitness goal
     * @return User's fitness goal
     */
    public String getGoal() {
        return goal;
    }

    /**
     * Set fitness goal
     * @param goal User's fitness goal
     */
    public void setGoal(String goal) {
        this.goal = goal;
    }

    /**
     * Get creation timestamp
     * @return When user account was created
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Set creation timestamp
     * Usually not called manually
     * @param createdAt Creation timestamp
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // ==================== UTILITY METHODS ====================

    /**
     * String representation of User object
     * Useful for debugging and logging
     *
     * @return User details as string
     */
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", age=" + age +
                ", height=" + height +
                ", weight=" + weight +
                ", goal='" + goal + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}

/**
 * USAGE EXAMPLES:
 *
 * 1. CREATE NEW USER:
 *    User user = new User("John Doe", "john@example.com", "password123",
 *                         25, 175, 70.0, "weight_loss");
 *    userRepository.save(user);
 *
 * 2. FIND USER BY EMAIL:
 *    User user = userRepository.findByEmail("john@example.com");
 *
 * 3. UPDATE USER WEIGHT:
 *    user.setWeight(68.5);
 *    userRepository.save(user);
 *
 * 4. DELETE USER:
 *    userRepository.delete(user);
 *
 * VALIDATION (Optional - add if needed):
 * Add these annotations for automatic validation:
 *
 * @NotBlank(message = "Username is required")
 * private String username;
 *
 * @Email(message = "Invalid email format")
 * private String email;
 *
 * @Size(min = 6, message = "Password must be at least 6 characters")
 * private String password;
 *
 * @Min(value = 10, message = "Age must be at least 10")
 * @Max(value = 120, message = "Age must be less than 120")
 * private Integer age;
 *
 * Then add dependency to pom.xml:
 * <dependency>
 *     <groupId>org.springframework.boot</groupId>
 *     <artifactId>spring-boot-starter-validation</artifactId>
 * </dependency>
 */