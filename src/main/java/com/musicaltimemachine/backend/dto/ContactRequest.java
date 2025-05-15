package com.musicaltimemachine.backend.dto;

public class ContactRequest {
    private String email;
    private String message;
    private String captchaToken;

    public String getEmail() {
        return email;
    }

    public void setEmail() {
        this.email = email;
    }

    public  String getMessage() {
        return message;
    }

    public void setMessage() {
        this.message = message;
    }

    public String getCaptchaToken() {
        return captchaToken;
    }

    public void setCaptchaToken(String captchaToken) {
        this.captchaToken = captchaToken;
    }
}

