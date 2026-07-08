package com.accenture.springai_bootcamp_demo.rag.dto;

import jakarta.validation.constraints.NotBlank;

public record RagRequest(@NotBlank String question) {
}
