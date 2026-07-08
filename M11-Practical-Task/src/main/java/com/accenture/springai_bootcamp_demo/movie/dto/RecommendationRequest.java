package com.accenture.springai_bootcamp_demo.movie.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

public record RecommendationRequest(
        @NotBlank String request,
        List<String> liked) {

    public List<String> liked() {
        return liked == null ? List.of() : liked;
    }
}
