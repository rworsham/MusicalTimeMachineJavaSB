package com.musicaltimemachine.backend.controller;

import com.musicaltimemachine.backend.service.SpotifyPlaylistService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/")
public class PlaylistController {


    private final SpotifyPlaylistService spotifyPlaylistService;

    public PlaylistController(SpotifyPlaylistService spotifyPlaylistService) {
        this.spotifyPlaylistService = spotifyPlaylistService;
    }

    @GetMapping("/playlist")
    public ResponseEntity<String> playlist(@RequestParam("date") String date, HttpSession session) {
        String accessToken = (String) session.getAttribute("spotifyAccessToken");

        if (accessToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Spotify access token not found");
        }

        String playlistData = spotifyPlaylistService.createPlaylistForDate(accessToken, date);

        return ResponseEntity.ok(playlistData);
    }
}
