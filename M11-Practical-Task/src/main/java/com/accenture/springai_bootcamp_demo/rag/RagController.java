package com.accenture.springai_bootcamp_demo.rag;

import com.accenture.springai_bootcamp_demo.rag.dto.RagAnswer;
import com.accenture.springai_bootcamp_demo.rag.dto.RagRequest;
import jakarta.validation.Valid;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rag")
@AllArgsConstructor
public class RagController {

    private final RagService ragService;

    @PostMapping("/ask")
    public RagAnswer ask(@Valid @RequestBody RagRequest request) {
        return ragService.ask(request.question());
    }
}
