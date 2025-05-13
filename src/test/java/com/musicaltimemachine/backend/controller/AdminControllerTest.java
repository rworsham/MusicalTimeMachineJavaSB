package com.musicaltimemachine.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.musicaltimemachine.backend.dto.LoginRequest;
import com.musicaltimemachine.backend.service.DataSeederService;
import com.musicaltimemachine.backend.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private DataSeederService dataSeederService;

    @Test
    void testSuccessfulLogin() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("admin");
        loginRequest.setPassword("password");

        Authentication mockAuth = Mockito.mock(Authentication.class);
        Mockito.when(mockAuth.isAuthenticated()).thenReturn(true);
        Mockito.when(authenticationManager.authenticate(Mockito.any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mockAuth);

        mockMvc.perform(post("/api/admin/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk());
    }


    @Test
    void testFailedLogin_InvalidCredentials() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("admin");
        loginRequest.setPassword("wrong");

        Mockito.when(authenticationManager.authenticate(Mockito.any()))
                .thenThrow(new BadCredentialsException("Invalid username or password"));

        mockMvc.perform(post("/api/admin/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Invalid username or password"));
    }

    @Test
    void testLogout() throws Exception {
        mockMvc.perform(post("/api/admin/logout"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Logout successful"));
    }

    @Test
    void testMeEndpoint_Authenticated() throws Exception {
        Authentication mockAuth = Mockito.mock(Authentication.class);
        Mockito.when(mockAuth.isAuthenticated()).thenReturn(true);
        Mockito.when(mockAuth.getName()).thenReturn("admin");

        Mockito.when(authenticationManager.authenticate(Mockito.any()))
                .thenReturn(mockAuth);

        SecurityContextHolder.getContext().setAuthentication(mockAuth);

        mockMvc.perform(get("/api/admin/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Authenticated"))
                .andExpect(jsonPath("$.user").value("admin"));
    }

    @Test
    void testMeEndpoint_Unauthenticated() throws Exception {
        SecurityContextHolder.clearContext();

        mockMvc.perform(get("/api/admin/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("User not authenticated"));
    }

    @Test
    void testSeedBillboard() throws Exception {
        mockMvc.perform(post("/api/admin/seed-billboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Seeding started check logs for progress"));

        Mockito.verify(dataSeederService).runAsyncSeeding();
    }
}
