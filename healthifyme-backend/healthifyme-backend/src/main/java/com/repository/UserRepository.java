package com.healthifyme.healthifyme_backend.repository;

import com.healthifyme.healthifyme_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * User Repository - Database operations for User entity
 *
 * FILE LOCATION: springboot-backend/src/main/java/com/healthifyme/repository/UserRepository.java
 *
 * HOW TO CREATE:
 * 1. Right-click 'com.healthifyme' → New → Package → Name: "repository"
 * 2. Right-click 'repository' → New → Java Class → Name: "UserRepository"
 * 3. Change "class" to "interface"
 * 4. Add "extends JpaRepository<User, Long>"
 *
 * WHAT THIS DOES:
 * JpaRepository provides automatic CRUD operations:
 * - save(user) - Create or update
 * - findById(id) - Find by ID
 * - findAll() - Get all users
 * - delete(user) - Delete user
 * - count() - Count users
 *
 * CUSTOM METHODS:
 * Just declare method names - Spring JPA implements them automatically!
 *
 * @author HealthifyMe Team
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find user by email address
     * Spring JPA automatically creates query: SELECT * FROM users WHERE email = ?
     *
     * @param email User's email
     * @return Optional containing user if found, empty otherwise
     *
     * USAGE:
     * Optional<User> userOpt = userRepository.findByEmail("john@example.com");
     * if (userOpt.isPresent()) {
     *     User user = userOpt.get();
     * }
     */
    Optional<User> findByEmail(String email);

    /**
     * Find user by username
     * Query: SELECT * FROM users WHERE username = ?
     *
     * @param username User's name
     * @return Optional containing user if found
     */
    Optional<User> findByUsername(String username);

    /**
     * Check if email already exists
     * Query: SELECT COUNT(*) > 0 FROM users WHERE email = ?
     *
     * @param email Email to check
     * @return true if email exists, false otherwise
     *
     * USAGE:
     * boolean exists = userRepository.existsByEmail("test@test.com");
     * if (exists) {
     *     // Email already registered
     * }
     */
    boolean existsByEmail(String email);

    /**
     * Find user by email and password (for login)
     * Query: SELECT * FROM users WHERE email = ? AND password = ?
     *
     * ⚠️ WARNING: This is for demo only!
     * In production, use Spring Security with BCrypt password hashing
     *
     * @param email User's email
     * @param password User's password (plain text)
     * @return Optional containing user if credentials match
     *
     * USAGE:
     * Optional<User> user = userRepository.findByEmailAndPassword(email, password);
     * if (user.isPresent()) {
     *     // Login successful
     * }
     */
    Optional<User> findByEmailAndPassword(String email, String password);
}

/**
 * MORE EXAMPLES OF AUTOMATIC QUERIES:
 *
 * Just declare method names - Spring creates queries automatically!
 *
 * List<User> findByGoal(String goal);
 * // SELECT * FROM users WHERE goal = ?
 *
 * List<User> findByAgeGreaterThan(Integer age);
 * // SELECT * FROM users WHERE age > ?
 *
 * List<User> findByAgeBetween(Integer minAge, Integer maxAge);
 * // SELECT * FROM users WHERE age BETWEEN ? AND ?
 *
 * List<User> findByUsernameContaining(String keyword);
 * // SELECT * FROM users WHERE username LIKE %keyword%
 *
 * Long countByGoal(String goal);
 * // SELECT COUNT(*) FROM users WHERE goal = ?
 *
 * void deleteByEmail(String email);
 * // DELETE FROM users WHERE email = ?
 *
 * CUSTOM QUERIES (Advanced):
 * @Query("SELECT u FROM User u WHERE u.weight > :weight AND u.goal = :goal")
 * List<User> findUsersWithWeightAndGoal(@Param("weight") Double weight, @Param("goal") String goal);
 */
