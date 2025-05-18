package com.musicaltimemachine.backend.controller;

import com.musicaltimemachine.backend.dto.ContactRequest;
import com.musicaltimemachine.backend.service.ContactService;
import com.musicaltimemachine.backend.service.RecaptchaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(ContactController.class);
    private final ContactService contactService;
    private final RecaptchaService recaptchaService;

    public ContactController(ContactService contactService, RecaptchaService recaptchaService) {
        this.contactService = contactService;
        this.recaptchaService = recaptchaService;
    }

    @PostMapping("/contact")
    public ResponseEntity<?> contact(
            @RequestBody ContactRequest contactRequest
    ) {
        try {
            boolean captchaValid = recaptchaService.verifyCaptcha(contactRequest.getCaptchaToken(), "contact_form_submit");

            if (!captchaValid) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "CAPTCHA validation failed"));
            }

            contactService.sendEmail(contactRequest);
            return ResponseEntity.ok(Map.of("message", "Contact message sent"));
        } catch (Exception e) {
            log.error("e: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Contact message failed"));
        }
    }

}
