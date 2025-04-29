package com.musicaltimemachine.backend.model.spotify;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AuthTokenResponse(
        @JsonProperty("access_token") String accessToken,
        @JsonProperty("token_type") String tokenType,
        @JsonProperty("expires_in") int expiresIn,
        @JsonProperty("refresh_token") String refreshToken,
        @JsonProperty("scope") String scope
) {}
