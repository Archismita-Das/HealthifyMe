package com.healthifyme.healthifyme_backend.repository;

import com.healthifyme.healthifyme_backend.model.WaterLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * WaterLog Repository - Database access for WaterLog entity
 *
 * FILE LOCATION: springboot-backend/src/main/java/com/healthifyme/repository/WaterLogRepository.java
 *
 * HOW TO CREATE:
 * Right-click 'repository' package → New → Java Class → Name: "WaterLogRepository"
 * Change "class" to "interface" and extend JpaRepository
 *
 * @author HealthifyMe Team
 */
@Repository
public interface WaterLogRepository extends JpaRepository<WaterLog, Long> {

    /**
     * Find water log for a user on a specific date
     * Query: SELECT * FROM water_logs WHERE user_id = ? AND date = ?
     *
     * @param userId User ID
     * @param date Date
     * @return Optional containing water log if exists
     *
     * USAGE:
     * Optional<WaterLog> log = waterLogRepository.findByUserIdAndDate(userId, LocalDate.now());
     * if (log.isPresent()) {
     *     Double liters = log.get().getLiters();
     * }
     */
    Optional<WaterLog> findByUserIdAndDate(Long userId, LocalDate date);

    /**
     * Find all water logs for a user
     * Query: SELECT * FROM water_logs WHERE user_id = ?
     *
     * @param userId User ID
     * @return List of user's water logs
     */
    List<WaterLog> findByUserId(Long userId);

    /**
     * Find water logs for date range
     * Query: SELECT * FROM water_logs WHERE user_id = ? AND date BETWEEN ? AND ?
     *
     * @param userId User ID
     * @param startDate Start date
     * @param endDate End date
     * @return List of water logs in range
     *
     * USAGE:
     * LocalDate today = LocalDate.now();
     * LocalDate weekAgo = today.minusDays(7);
     * List<WaterLog> weekLogs = waterLogRepository.findByUserIdAndDateBetween(userId, weekAgo, today);
     */
    List<WaterLog> findByUserIdAndDateBetween(Long userId, LocalDate startDate, LocalDate endDate);

    /**
     * Find water logs ordered by date (most recent first)
     * Query: SELECT * FROM water_logs WHERE user_id = ? ORDER BY date DESC
     *
     * @param userId User ID
     * @return List of water logs ordered by date
     */
    List<WaterLog> findByUserIdOrderByDateDesc(Long userId);

    /**
     * Check if water log exists for user and date
     * Query: SELECT COUNT(*) > 0 FROM water_logs WHERE user_id = ? AND date = ?
     *
     * @param userId User ID
     * @param date Date
     * @return true if log exists
     */
    boolean existsByUserIdAndDate(Long userId, LocalDate date);

    /**
     * Delete water log for specific date
     * Query: DELETE FROM water_logs WHERE user_id = ? AND date = ?
     *
     * @param userId User ID
     * @param date Date
     */
    void deleteByUserIdAndDate(Long userId, LocalDate date);
}

/**
 * USAGE EXAMPLES IN CONTROLLER:
 *
 * 1. LOG WATER (ADD TO EXISTING):
 *    @PostMapping("/water/add")
 *    public WaterLog addWater(@RequestParam Long userId, @RequestParam Double liters) {
 *        LocalDate today = LocalDate.now();
 *        WaterLog log = waterLogRepository.findByUserIdAndDate(userId, today)
 *            .orElse(new WaterLog(userId, today, 0.0));
 *        log.setLiters(log.getLiters() + liters);
 *        return waterLogRepository.save(log);
 *    }
 *
 * 2. GET TODAY'S WATER INTAKE:
 *    Optional<WaterLog> todayLog = waterLogRepository.findByUserIdAndDate(userId, LocalDate.now());
 *    Double liters = todayLog.map(WaterLog::getLiters).orElse(0.0);
 *
 * 3. GET LAST 7 DAYS:
 *    LocalDate today = LocalDate.now();
 *    LocalDate weekAgo = today.minusDays(7);
 *    List<WaterLog> weekLogs = waterLogRepository.findByUserIdAndDateBetween(userId, weekAgo, today);
 */
