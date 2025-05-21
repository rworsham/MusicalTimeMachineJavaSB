package com.musicaltimemachine.backend.service;

import com.musicaltimemachine.backend.dto.PlaylistLogEntry;
import com.musicaltimemachine.backend.dto.PlaylistLogStatsDetail;
import com.musicaltimemachine.backend.entity.PlaylistLog;
import com.musicaltimemachine.backend.repository.PlaylistLogRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PlaylistLogService {

    private final PlaylistLogRepository playlistLogRepository;

    public PlaylistLogService(PlaylistLogRepository playlistLogRepository) {
        this.playlistLogRepository = playlistLogRepository;
    }

    public PlaylistLogStatsDetail getPlaylistLogsForToday() {
        LocalDateTime now = LocalDateTime.now();

        LocalDateTime startOfLast24Hours = now.minusHours(24);

        LocalDateTime startOfLast7Days = now.minusDays(7);

        List<PlaylistLog> logsLast24Hours = playlistLogRepository.findByCreatedAtToday(startOfLast24Hours, now);
        List<PlaylistLog> logsLast7Days = playlistLogRepository.findByCreatedAtThisWeek(startOfLast7Days, now);

        List<PlaylistLogEntry> logEntries = logsLast7Days.stream()
                .map(log -> new PlaylistLogEntry(
                        log.getCreatedAt().toString(),
                        log.getRequestedChartDate().toString(),
                        log.isPublic()
                ))
                .toList();

        return new PlaylistLogStatsDetail(
                logsLast24Hours.size(),
                logsLast7Days.size(),
                logEntries
        );
    }
}
