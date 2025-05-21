package com.musicaltimemachine.backend.controller;

import com.musicaltimemachine.backend.dto.PlaylistLogStatsDetail;
import com.musicaltimemachine.backend.service.PlaylistLogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/")
public class AdminLogController {

    private final PlaylistLogService playlistLogService;

    public AdminLogController(PlaylistLogService playlistLogService) {
        this.playlistLogService = playlistLogService;
    }

    @GetMapping("/logs")
    public ResponseEntity<PlaylistLogStatsDetail> playlistLogs() {
        PlaylistLogStatsDetail playlistLogs = playlistLogService.getPlaylistLogsForToday();
        return ResponseEntity.ok(playlistLogs);
    }

}
