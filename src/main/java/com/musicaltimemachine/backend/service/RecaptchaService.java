package com.musicaltimemachine.backend.service;

import com.musicaltimemachine.backend.dto.RecaptchaResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RecaptchaService {

    @Value("${RECAPTCHA_SECRET}")
    private String recaptchaSecret;

    private final RestTemplate restTemplate;

    public RecaptchaService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public boolean verifyCaptcha(String captchaToken, String expectedAction) {
        String url = "https://www.google.com/recaptcha/api/siteverify";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String requestBody = "secret=" + recaptchaSecret + "&response=" + captchaToken;
        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<RecaptchaResponse> response = restTemplate.postForEntity(
                url,
                entity,
                RecaptchaResponse.class
        );

        RecaptchaResponse recaptcha = response.getBody();

        if (recaptcha == null || !recaptcha.isSuccess()) {
            return false;
        }

        if (recaptcha.getScore() < 0.5f) {
            return false;
        }

        if (!expectedAction.equals(recaptcha.getAction())) {
            return false;
        }

        return true;
    }
}
