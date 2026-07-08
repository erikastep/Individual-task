package com.accenture.springai_bootcamp_demo.movie;

import com.accenture.springai_bootcamp_demo.movie.dto.MovieRecommendation;
import com.accenture.springai_bootcamp_demo.movie.dto.RecommendationRequest;
import jakarta.validation.Valid;
import java.util.List;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/movies")
@AllArgsConstructor
public class MovieController {

    private final MovieRecommendationService recommendationService;

    @PostMapping("/recommend")
    public List<MovieRecommendation> recommend(@Valid @RequestBody RecommendationRequest request) {
        return recommendationService.recommend(request);
    }
}
