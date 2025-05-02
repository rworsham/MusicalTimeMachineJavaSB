package com.musicaltimemachine.backend.dto.spotify;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PlaylistRequest(
        String name,
        String description,
        boolean isPublic
) {
    @JsonProperty("public")
    public boolean isPublic() {
        return isPublic;
    }
}