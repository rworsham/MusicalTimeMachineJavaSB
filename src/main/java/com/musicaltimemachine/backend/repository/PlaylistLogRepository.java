package com.musicaltimemachine.backend.repository;

import com.musicaltimemachine.backend.entity.PlaylistLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PlaylistLogRepository extends JpaRepository<PlaylistLog, Long> {
    @Query("SELECT p FROM PlaylistLog p WHERE p.createdAt BETWEEN :startOfDay AND :endOfDay")
    List<PlaylistLog> findByCreatedAtToday(LocalDateTime startOfDay, LocalDateTime endOfDay);

    @Query("SELECT p FROM PlaylistLog p WHERE p.createdAt BETWEEN :startOfWeek AND :endOfWeek")
    List<PlaylistLog> findByCreatedAtThisWeek(LocalDateTime startOfWeek, LocalDateTime endOfWeek);

    @Query("SELECT p FROM PlaylistLog p WHERE p.createdAt BETWEEN :startOfMonth AND :endOfMonth")
    List<PlaylistLog> findByCreatedAtThisMonth(LocalDateTime startOfMonth, LocalDateTime endOfMonth);
}
