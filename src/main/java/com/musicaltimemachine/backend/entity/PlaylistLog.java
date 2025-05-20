package com.musicaltimemachine.backend.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name= "playlist_log",indexes = @Index(columnList = "createdAt"))
public class PlaylistLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime createdAt;

    private LocalDate requestedChartDate;

    private boolean isPublic;

    public PlaylistLog() {
    }

    public PlaylistLog(LocalDateTime createdAt, LocalDate requestedChartDate, boolean isPublic) {
        this.createdAt = createdAt;
        this.requestedChartDate = requestedChartDate;
        this.isPublic = isPublic;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDate getRequestedChartDate() {
        return requestedChartDate;
    }

    public void setRequestedChartDate(LocalDate requestedChartDate) {
        this.requestedChartDate = requestedChartDate;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }
}
