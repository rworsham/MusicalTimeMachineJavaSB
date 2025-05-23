package com.musicaltimemachine.backend.service;

import com.musicaltimemachine.backend.dto.spotify.PlaylistRequest;
import com.musicaltimemachine.backend.dto.spotify.PlaylistResponse;
import com.musicaltimemachine.backend.repository.SongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Collections;

@Service
public class SpotifyPlaylistService {

    private final RestTemplate restTemplate;
    private final SongRepository songRepository;
    private final PlaylistLogService playlistLogService;

    @Autowired
    public SpotifyPlaylistService(RestTemplate restTemplate, SongRepository songRepository, PlaylistLogService playlistLogService) {
        this.restTemplate = restTemplate;
        this.songRepository = songRepository;
        this.playlistLogService = playlistLogService;
    }


    public PlaylistResponse createPlaylistForDate(String accessToken, String date, Boolean isPublic) {
        LocalDate parsedDate = LocalDate.parse(date);

        List<String> uris = songRepository.findUrisByChartDate(parsedDate);

        Collections.shuffle(uris);

        String userId = getCurrentUserSpotifyId(accessToken);

        String playlistName = "Top Songs for " + date;
        String playlistDescription = "A playlist with the top songs from " + date +
                ". Created by the Musical Time Machine Service!";

        String createPlaylistUrl = "https://api.spotify.com/v1/users/" + userId + "/playlists";

        PlaylistRequest playlistRequest = new PlaylistRequest(playlistName, playlistDescription, isPublic);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<PlaylistRequest> requestEntity = new HttpEntity<>(playlistRequest, headers);

        ResponseEntity<PlaylistResponse> response = restTemplate.exchange(
                createPlaylistUrl,
                HttpMethod.POST,
                requestEntity,
                PlaylistResponse.class
        );

        PlaylistResponse playlist = response.getBody();
        if (playlist == null || playlist.id() == null) {
            throw new IllegalStateException("Failed to create playlist");
        }

        String addTracksUrl = "https://api.spotify.com/v1/playlists/" + playlist.id() + "/tracks";
        Map<String, Object> addTracksBody = Map.of("uris", uris);

        HttpEntity<Map<String, Object>> addTracksRequest = new HttpEntity<>(addTracksBody, headers);
        restTemplate.postForEntity(addTracksUrl, addTracksRequest, Void.class);
        playlistLogService.saveLog(parsedDate, isPublic);

        return playlist;
    }


    private String  getCurrentUserSpotifyId (String accessToken) {
        String url = "https://api.spotify.com/v1/me";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                Map.class
        );

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            Map<String, Object> userData = response.getBody();
            return (String) userData.get("id");
        } else {
            throw new RuntimeException("Failed to fetch Spotify user ID");
        }
    }
}
