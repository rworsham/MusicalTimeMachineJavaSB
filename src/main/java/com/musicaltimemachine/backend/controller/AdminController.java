package com.musicaltimemachine.backend.controller;

import com.musicaltimemachine.backend.service.DataSeederService;
import com.musicaltimemachine.backend.service.UserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final DataSeederService dataSeederService;

    public AdminController(UserService userService,
                           AuthenticationManager authenticationManager,
                           DataSeederService dataSeederService) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.dataSeederService = dataSeederService;
    }

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password) {
        try {
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(username, password);

            Authentication authentication = authenticationManager.authenticate(authenticationToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            return "Login successful";
        } catch (Exception e) {
            return "Authentication failed: " + e.getMessage();
        }
    }

    @PostMapping("/seed-billboard")
    public String startBillboardSeeding() {
        dataSeederService.runAsyncSeeding();
        return "Seeding process started. Check logs for progress.";
    }
}