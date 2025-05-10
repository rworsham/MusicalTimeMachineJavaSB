package com.musicaltimemachine.backend.service;

import com.musicaltimemachine.backend.entity.Song;
import com.musicaltimemachine.backend.repository.SongRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Service
public class DataSeederService {

    private final SongRepository songRepository;
    private final SpotifyAuthService spotifyAuthService;
    private final RestTemplate restTemplate;

    @Autowired
    public DataSeederService(SongRepository songRepository, SpotifyAuthService spotifyAuthService, RestTemplate restTemplate) {
        this.songRepository = songRepository;
        this.spotifyAuthService = spotifyAuthService;
        this.restTemplate = restTemplate;
    }

    @Async
    public void runAsyncSeeding() {
        seedBillboardData();
    }

    public void seedBillboardData() {
        LocalDate startDate = LocalDate.of(1959, 8, 3);
        LocalDate endDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        LocalDate current = startDate;

        while (current.isBefore(endDate)) {
            try {
                if (!songRepository.findByChartDate(current).isEmpty()) {
                    System.out.println("Skipping " + current + " — already in database.");
                    current = current.plusWeeks(1);
                    continue;
                }

                String formattedDate = current.format(formatter);
                String url = "https://www.billboard.com/charts/hot-100/" + formattedDate + "/";

                System.out.println("Fetching: " + url);

                Document doc = Jsoup.connect(url).get();

                Elements songElements = doc.select("li h3#title-of-a-story");
                Elements artistElements = doc.select("span.a-no-trucate");

                List<Song> songs = new ArrayList<>();

                int count = Math.min(songElements.size(), artistElements.size());
                for (int i = 0; i < count; i++) {
                    String title = songElements.get(i).text().trim();
                    String artist = artistElements.get(i).text().trim();

                    String uri = getUriFromSpotify(title, artist);

                    Song song = new Song(title, artist, uri, current);
                    songs.add(song);
                }

                songRepository.saveAll(songs);

                System.out.println("Saved " + songs.size() + " songs for " + formattedDate);

                Thread.sleep(3000);

            } catch (IOException e) {
                System.err.println("Failed to fetch " + current + ": " + e.getMessage());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }

            current = current.plusWeeks(1);
        }
    }

    private String getUriFromSpotify(String title, String artist) {
        try {
            String accessToken = spotifyAuthService.getClientAccessToken();
            Thread.sleep(1000 + new Random().nextInt(1000));

            String query = String.format("track:%s artist:%s", title, artist);
            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
            String url = "https://api.spotify.com/v1/search?q=" + encodedQuery + "&type=track&limit=1";

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    requestEntity,
                    String.class
            );

            JsonNode root = new ObjectMapper().readTree(response.getBody());
            JsonNode uriNode = root.path("tracks").path("items").path(0).path("uri");

            return uriNode.isMissingNode() ? null : uriNode.asText();

        } catch (HttpClientErrorException.TooManyRequests e) {
            String retryAfter = e.getResponseHeaders().getFirst("Retry-After");
            long waitSeconds = retryAfter != null ? Long.parseLong(retryAfter) : 5;
            System.err.println("429 Rate limited by Spotify. Retrying after " + waitSeconds + " seconds.");
            try {
                Thread.sleep(waitSeconds * 1000L);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
            return getUriFromSpotify(title, artist);

        } catch (Exception e) {
            System.err.println("Failed to fetch URI from Spotify for '" + title + "' by '" + artist + "': " + e.getMessage());
        }

        return null;
    }
}