package com.healthifyme.healthifyme_backend.controller;

import com.healthifyme.healthifyme_backend.model.WaterLog;
import com.healthifyme.healthifyme_backend.repository.WaterLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * WaterLog Controller - REST API endpoints for water tracking
 *
 * FILE LOCATION: springboot-backend/src/main/java/com/healthifyme/controller/WaterLogController.java
 *
 * HOW TO CREATE:
 * 1. Right-click 'controller' package → New → Java Class → Name: "WaterLogController"
 * 2. Paste this code
 *
 * WHAT THIS DOES:
 * Provides REST API endpoints for tracking daily water intake
 *
 * ENDPOINTS PROVIDED:
 * POST   /api/water/log            - Log/update water intake
 * GET    /api/water/today/{userId} - Get today's water intake
 * GET    /api/water/history/{userId} - Get water log history
 * GET    /api/water/stats/{userId} - Get water statistics
 * DELETE /api/water/{userId}/{date} - Delete water log
 *
 * BASE URL: http://localhost:8081/api/water
 *
 * @author HealthifyMe Team
 */
@RestController
@RequestMapping("/api/water")
@CrossOrigin(origins = "*")
public class WaterLogController {

    @Autowired
    private WaterLogRepository waterLogRepository;

    // ==================== ENDPOINTS ====================

    /**
     * LOG/UPDATE WATER INTAKE
     *
     * ENDPOINT: POST /api/water/log
     * URL: http://localhost:8081/api/water/log
     *
     * REQUEST BODY (JSON):
     * {
     *   "userId": 1,
     *   "date": "2025-10-25",
     *   "liters": 0.5
     * }
     *
     * BEHAVIOR:
     * - If log exists for that date, REPLACES the value
     * - If log doesn't exist, creates new entry
     *
     * RESPONSE:
     * {
     *   "id": 1,
     *   "userId": 1,
     *   "date": "2025-10-25",
     *   "liters": 0.5,
     *   "createdAt": "2025-10-25T10:30:00"
     * }
     *
     * TEST WITH CURL:
     * curl -X POST http://localhost:8081/api/water/log \
     *   -H "Content-Type: application/json" \
     *   -d '{"userId":1,"date":"2025-10-25","liters":0.5}'
     *
     * @param waterLog Water log object
     * @return Saved water log
     */
    @PostMapping("/log")
    public ResponseEntity<WaterLog> logWater(@RequestBody WaterLog waterLog) {
        // Validate input
        if (waterLog.getUserId() == null || waterLog.getDate() == null || waterLog.getLiters() == null) {
            return ResponseEntity.badRequest().build();
        }

        if (waterLog.getLiters() < 0) {
            return ResponseEntity.badRequest().build();
        }

        // Check if log exists for this date
        Optional<WaterLog> existingLog = waterLogRepository
                .findByUserIdAndDate(waterLog.getUserId(), waterLog.getDate());

        if (existingLog.isPresent()) {
            // Update existing log
            WaterLog log = existingLog.get();
            log.setLiters(waterLog.getLiters());
            return ResponseEntity.ok(waterLogRepository.save(log));
        } else {
            // Create new log
            return ResponseEntity.ok(waterLogRepository.save(waterLog));
        }
    }

    /**
     * ADD WATER TO TODAY'S LOG
     *
     * ENDPOINT: POST /api/water/add?userId=1&liters=0.25
     * URL: http://localhost:8081/api/water/add?userId=1&liters=0.25
     *
     * PARAMETERS:
     * - userId: User ID
     * - liters: Amount to add (e.g., 0.25 for 250ml)
     *
     * BEHAVIOR:
     * - ADDS to existing value (doesn't replace)
     * - If no log exists, creates new with this amount
     *
     * EXAMPLES:
     * /api/water/add?userId=1&liters=0.25  → Add 250ml
     * /api/water/add?userId=1&liters=0.5   → Add 500ml
     * /api/water/add?userId=1&liters=1.0   → Add 1 liter
     *
     * TEST WITH CURL:
     * curl -X POST "http://localhost:8081/api/water/add?userId=1&liters=0.25"
     *
     * @param userId User ID
     * @param liters Amount to add
     * @return Updated water log
     */
    @PostMapping("/add")
    public ResponseEntity<WaterLog> addWater(
            @RequestParam Long userId,
            @RequestParam Double liters) {

        if (liters <= 0) {
            return ResponseEntity.badRequest().build();
        }

        LocalDate today = LocalDate.now();

        // Get existing log or create new
        WaterLog log = waterLogRepository.findByUserIdAndDate(userId, today)
                .orElse(new WaterLog(userId, today, 0.0));

        // Add to existing amount
        log.setLiters(log.getLiters() + liters);

        return ResponseEntity.ok(waterLogRepository.save(log));
    }

    /**
     * GET TODAY'S WATER INTAKE
     *
     * ENDPOINT: GET /api/water/today/{userId}
     * URL: http://localhost:8081/api/water/today/1
     *
     * RETURNS: Today's water log or 404 if not found
     *
     * RESPONSE:
     * {
     *   "id": 1,
     *   "userId": 1,
     *   "date": "2025-10-25",
     *   "liters": 2.5
     * }
     *
     * TEST WITH CURL:
     * curl http://localhost:8081/api/water/today/1
     *
     * @param userId User ID
     * @return Today's water log
     */
    @GetMapping("/today/{userId}")
    public ResponseEntity<WaterLog> getTodayWater(@PathVariable Long userId) {
        LocalDate today = LocalDate.now();
        Optional<WaterLog> log = waterLogRepository.findByUserIdAndDate(userId, today);

        return log.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * GET WATER INTAKE FOR SPECIFIC DATE
     *
     * ENDPOINT: GET /api/water/{userId}/{date}
     * URL: http://localhost:8081/api/water/1/2025-10-25
     *
     * @param userId User ID
     * @param date Date (YYYY-MM-DD)
     * @return Water log for that date
     */
    @GetMapping("/{userId}/{date}")
    public ResponseEntity<WaterLog> getWaterByDate(
            @PathVariable Long userId,
            @PathVariable String date) {

        try {
            LocalDate localDate = LocalDate.parse(date);
            Optional<WaterLog> log = waterLogRepository.findByUserIdAndDate(userId, localDate);

            return log.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * GET WATER LOG HISTORY
     *
     * ENDPOINT: GET /api/water/history/{userId}?days=7
     * URL: http://localhost:8081/api/water/history/1?days=7
     *
     * PARAMETERS:
     * - days: Number of days to retrieve (default: 7)
     *
     * RETURNS: Last N days of water logs (most recent first)
     *
     * RESPONSE:
     * [
     *   {"id": 3, "userId": 1, "date": "2025-10-25", "liters": 2.5},
     *   {"id": 2, "userId": 1, "date": "2025-10-24", "liters": 2.0},
     *   {"id": 1, "userId": 1, "date": "2025-10-23", "liters": 1.5}
     * ]
     *
     * TEST WITH CURL:
     * curl "http://localhost:8081/api/water/history/1?days=7"
     *
     * @param userId User ID
     * @param days Number of days (optional, default 7)
     * @return List of water logs
     */
    @GetMapping("/history/{userId}")
    public ResponseEntity<List<WaterLog>> getWaterHistory(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "7") int days) {

        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusDays(days - 1);

        List<WaterLog> logs = waterLogRepository
                .findByUserIdAndDateBetween(userId, startDate, today);

        return ResponseEntity.ok(logs);
    }

    /**
     * GET ALL WATER LOGS (FOR ADMIN/TESTING)
     *
     * ENDPOINT: GET /api/water/all/{userId}
     * URL: http://localhost:8081/api/water/all/1
     *
     * @param userId User ID
     * @return All water logs for user
     */
    @GetMapping("/all/{userId}")
    public ResponseEntity<List<WaterLog>> getAllWaterLogs(@PathVariable Long userId) {
        List<WaterLog> logs = waterLogRepository.findByUserIdOrderByDateDesc(userId);
        return ResponseEntity.ok(logs);
    }

    /**
     * GET WATER STATISTICS
     *
     * ENDPOINT: GET /api/water/stats/{userId}?days=7
     * URL: http://localhost:8081/api/water/stats/1?days=7
     *
     * RETURNS: Statistics for last N days
     *
     * RESPONSE:
     * {
     *   "totalLiters": 14.5,
     *   "averagePerDay": 2.07,
     *   "daysLogged": 7,
     *   "maxInDay": 3.0,
     *   "minInDay": 1.5,
     *   "goalMet": 5  // Days where >= 2 liters
     * }
     *
     * @param userId User ID
     * @param days Number of days for stats
     * @return Statistics map
     */
    @GetMapping("/stats/{userId}")
    public ResponseEntity<Map<String, Object>> getWaterStats(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "7") int days) {

        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusDays(days - 1);

        List<WaterLog> logs = waterLogRepository
                .findByUserIdAndDateBetween(userId, startDate, today);

        Map<String, Object> stats = new HashMap<>();

        if (logs.isEmpty()) {
            stats.put("totalLiters", 0.0);
            stats.put("averagePerDay", 0.0);
            stats.put("daysLogged", 0);
            stats.put("maxInDay", 0.0);
            stats.put("minInDay", 0.0);
            stats.put("goalMet", 0);
            return ResponseEntity.ok(stats);
        }

        // Calculate statistics
        double total = logs.stream().mapToDouble(WaterLog::getLiters).sum();
        double max = logs.stream().mapToDouble(WaterLog::getLiters).max().orElse(0.0);
        double min = logs.stream().mapToDouble(WaterLog::getLiters).min().orElse(0.0);
        long goalMet = logs.stream().filter(log -> log.getLiters() >= 2.0).count();

        stats.put("totalLiters", Math.round(total * 10.0) / 10.0);
        stats.put("averagePerDay", Math.round((total / days) * 100.0) / 100.0);
        stats.put("daysLogged", logs.size());
        stats.put("maxInDay", max);
        stats.put("minInDay", min);
        stats.put("goalMet", goalMet);

        return ResponseEntity.ok(stats);
    }

    /**
     * DELETE WATER LOG
     *
     * ENDPOINT: DELETE /api/water/{userId}/{date}
     * URL: http://localhost:8081/api/water/1/2025-10-25
     *
     * TEST WITH CURL:
     * curl -X DELETE http://localhost:8081/api/water/1/2025-10-25
     *
     * @param userId User ID
     * @param date Date to delete
     * @return Success response
     */
    @DeleteMapping("/{userId}/{date}")
    public ResponseEntity<Map<String, String>> deleteWaterLog(
            @PathVariable Long userId,
            @PathVariable String date) {

        try {
            LocalDate localDate = LocalDate.parse(date);
            waterLogRepository.deleteByUserIdAndDate(userId, localDate);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Water log deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}

/**
 * TESTING GUIDE:
 *
 * 1. START APPLICATION:
 *    Run HealthifyMeApplication.java
 *
 * 2. TEST LOGGING WATER (Postman):
 *    POST http://localhost:8081/api/water/log
 *    Body (JSON):
 *    {
 *      "userId": 1,
 *      "date": "2025-10-25",
 *      "liters": 0.5
 *    }
 *
 * 3. TEST ADDING WATER:
 *    POST http://localhost:8081/api/water/add?userId=1&liters=0.25
 *
 * 4. TEST GET TODAY'S WATER:
 *    GET http://localhost:8081/api/water/today/1
 *
 * 5. TEST GET HISTORY:
 *    GET http://localhost:8081/api/water/history/1?days=7
 *
 * 6. TEST GET STATS:
 *    GET http://localhost:8081/api/water/stats/1?days=7
 *
 * FRONTEND INTEGRATION EXAMPLE:
 *
 * // Add 250ml of water
 * async function addWater() {
 *     const response = await fetch(
 *         'http://localhost:8081/api/water/add?userId=1&liters=0.25',
 *         { method: 'POST' }
 *     );
 *     const log = await response.json();
 *     console.log('Current water intake:', log.liters, 'liters');
 * }
 *
 * // Get today's intake
 * async function getTodayWater() {
 *     const response = await fetch('http://localhost:8081/api/water/today/1');
 *     const log = await response.json();
 *     document.getElementById('waterDisplay').textContent = log.liters + ' L';
 * }
 */