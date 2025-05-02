package com.musicaltimemachine.backend.dto.spotify;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PlaylistResponse(
        String id,
        String name,
        @JsonProperty("external_urls") ExternalUrls externalUrls
) {
    public record ExternalUrls(String spotify) {}
}