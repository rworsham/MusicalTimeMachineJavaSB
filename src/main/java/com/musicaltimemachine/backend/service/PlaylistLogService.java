package com.musicaltimemachine.backend.service;

import com.musicaltimemachine.backend.dto.PlaylistLogEntry;
import com.musicaltimemachine.backend.dto.PlaylistLogStatsDetail;
import com.musicaltimemachine.backend.entity.PlaylistLog;
import com.musicaltimemachine.backend.repository.PlaylistLogRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PlaylistLogService {

    private final PlaylistLogRepository playlistLogRepository;

    public PlaylistLogService(PlaylistLogRepository playlistLogRepository) {
        this.playlistLogRepository = playlistLogRepository;
    }

    public PlaylistLogStatsDetail getPlaylistLogs() {
        LocalDateTime now = LocalDateTime.now();

        LocalDateTime startOfLast24Hours = now.minusHours(24);

        LocalDateTime startOfLast7Days = now.minusDays(7);

        LocalDateTime startOfLast30Days = now.minusDays(30);

        List<PlaylistLog> logsLast24Hours = playlistLogRepository.findByCreatedAtToday(startOfLast24Hours, now);
        List<PlaylistLog> logsLast7Days = playlistLogRepository.findByCreatedAtThisWeek(startOfLast7Days, now);
        List<PlaylistLog> logsLast30Days = playlistLogRepository.findByCreatedAtThisMonth(startOfLast30Days, now);

        List<PlaylistLogEntry> logEntries = logsLast30Days.stream()
                .map(log -> new PlaylistLogEntry(
                        log.getCreatedAt().toString(),
                        log.getRequestedChartDate().toString(),
                        log.isPublic()
                ))
                .toList();

        return new PlaylistLogStatsDetail(
                logsLast24Hours.size(),
                logsLast7Days.size(),
                logsLast30Days.size(),
                logEntries
        );
    }

    public void saveLog(LocalDate chartDate, boolean isPublic) {
        PlaylistLog log = new PlaylistLog();
        log.setCreatedAt(LocalDateTime.now());
        log.setRequestedChartDate(chartDate);
        log.setPublic(isPublic);
        playlistLogRepository.save(log);
    }
}
