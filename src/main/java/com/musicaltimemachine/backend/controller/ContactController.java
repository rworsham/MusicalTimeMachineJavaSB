package com.musicaltimemachine.backend.controller;

import com.musicaltimemachine.backend.dto.ContactRequest;
import com.musicaltimemachine.backend.service.ContactService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/")
public class ContactController {

    private final ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @PostMapping("/contact")
    public ResponseEntity<?> contact(
            @RequestBody ContactRequest contactRequest
    ) {
        try {
            boolean captchaValid = contactService.verifyCaptcha(contactRequest.getCaptchaToken());

            if (!captchaValid) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "CAPTCHA validation failed"));
            }

            contactService.sendEmail(contactRequest);
            return ResponseEntity.ok(Map.of("message", "Contact message sent"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Contact message failed"));
        }
    }

}
