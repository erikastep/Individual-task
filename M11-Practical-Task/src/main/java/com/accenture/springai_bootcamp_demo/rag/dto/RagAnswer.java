package com.accenture.springai_bootcamp_demo.rag.dto;

import java.util.List;

public record RagAnswer(String answer, List<String> sources) {
}
