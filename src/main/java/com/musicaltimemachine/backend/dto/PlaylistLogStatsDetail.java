package com.musicaltimemachine.backend.dto;

import java.util.List;

public record PlaylistLogStatsDetail(
        long todayCount,
        long thisWeekCount,
        long thisMonthCount,
        List<PlaylistLogEntry> logs
) {}