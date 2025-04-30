package com.musicaltimemachine.backend.service;

import com.musicaltimemachine.backend.dto.spotify.AuthTokenResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Base64;

@Service
public class SpotifyAuthService {

    @Value("${spotify.client-id}")
    private String clientId;

    @Value("${spotify.client-secret}")
    private String clientSecret;

    @Value("${spotify.redirect-uri}")
    private String redirectUri;

    private final RestTemplate restTemplate;

    public SpotifyAuthService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String exchangeCodeForAccessToken(String code) {
        String tokenUrl = "https://accounts.spotify.com/api/token";

        String requestBody = "grant_type=authorization_code" +
                "&code=" + code +
                "&redirect_uri=" + redirectUri;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", "Basic " + encodeClientCredentials());

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<AuthTokenResponse> response = restTemplate.exchange(
                tokenUrl,
                HttpMethod.POST,
                entity,
                AuthTokenResponse.class
        );

        AuthTokenResponse tokenResponse = response.getBody();
        if (tokenResponse == null) {
            throw new RuntimeException("No token returned from Spotify");
        }

        return tokenResponse.accessToken();
    }

    private String encodeClientCredentials() {
        String clientCredentials = clientId + ":" + clientSecret;
        return Base64.getEncoder().encodeToString(clientCredentials.getBytes());
    }
}
