package com.musicaltimemachine.backend.controller;

import com.musicaltimemachine.backend.service.SpotifyAuthService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "spotify.client-id=test-client-id",
        "spotify.redirect-uri=http://localhost/callback"
})
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SpotifyAuthService spotifyAuthService;

    @Test
    void testLoginRedirectsToSpotifyAuth() throws Exception {
        mockMvc.perform(get("/api/auth/login"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", containsString("https://accounts.spotify.com/authorize")))
                .andExpect(header().string("Location", containsString("client_id=test-client-id")))
                .andExpect(header().string("Location", containsString("redirect_uri=http%3A%2F%2Flocalhost%2Fcallback")))
                .andExpect(header().string("Location", containsString("scope=")));
    }

    @Test
    void testCallbackExchangesCodeAndRedirects() throws Exception {
        Mockito.when(spotifyAuthService.exchangeCodeForAccessToken("abc123"))
                .thenReturn("fake-token");

        mockMvc.perform(get("/api/auth/callback?code=abc123"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "http://localhost:5173/timetravel"));
    }
}