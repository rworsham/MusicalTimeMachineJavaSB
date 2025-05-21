package com.musicaltimemachine.backend.dto;

public record PlaylistLogEntry(
        String createdAt,
        String requestedChartDate,
        boolean isPublic
) {}