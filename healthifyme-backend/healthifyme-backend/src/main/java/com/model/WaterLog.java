package com.healthifyme.healthifyme_backend.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * WaterLog Entity - Tracks daily water intake
 *
 * FILE LOCATION: springboot-backend/src/main/java/com/healthifyme/model/WaterLog.java
 *
 * HOW TO CREATE:
 * Right-click 'model' package → New → Java Class → Name: "WaterLog"
 *
 * PURPOSE:
 * Records user's daily water consumption in liters
 * Maps to 'water_logs' table in MySQL
 *
 * @author HealthifyMe Team
 */
@Entity
@Table(name = "water_logs")
public class WaterLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Foreign key to users table
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * Date of water log
     */
    @Column(nullable = false)
    private LocalDate date;

    /**
     * Water consumed in liters
     */
    @Column(nullable = false)
    private Double liters;

    /**
     * Timestamp when log was created
     */
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // ==================== CONSTRUCTORS ====================

    public WaterLog() {
    }

    public WaterLog(Long userId, LocalDate date, Double liters) {
        this.userId = userId;
        this.date = date;
        this.liters = liters;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
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

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Double getLiters() {
        return liters;
    }

    public void setLiters(Double liters) {
        this.liters = liters;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "WaterLog{" +
                "id=" + id +
                ", userId=" + userId +
                ", date=" + date +
                ", liters=" + liters +
                '}';
    }
}

/**
 * USAGE EXAMPLES:
 *
 * 1. LOG WATER:
 *    WaterLog log = new WaterLog(userId, LocalDate.now(), 0.5); // 500ml
 *    waterLogRepository.save(log);
 *
 * 2. GET TODAY'S WATER INTAKE:
 *    Optional<WaterLog> todayLog = waterLogRepository.findByUserIdAndDate(userId, LocalDate.now());
 *
 * 3. UPDATE WATER INTAKE:
 *    WaterLog log = waterLogRepository.findByUserIdAndDate(userId, LocalDate.now())
 *        .orElse(new WaterLog(userId, LocalDate.now(), 0.0));
 *    log.setLiters(log.getLiters() + 0.25); // Add 250ml
 *    waterLogRepository.save(log);
 */
