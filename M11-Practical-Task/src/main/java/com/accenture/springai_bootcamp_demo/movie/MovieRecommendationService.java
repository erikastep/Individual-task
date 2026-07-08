package com.accenture.springai_bootcamp_demo.movie;

import com.accenture.springai_bootcamp_demo.movie.dto.MovieCandidate;
import com.accenture.springai_bootcamp_demo.movie.dto.MovieRecommendation;
import com.accenture.springai_bootcamp_demo.movie.dto.RecommendationRequest;
import java.util.List;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

/**
 * Two-agent movie recommender: a proposer agent suggests candidate movies from
 * the model's knowledge, then a curator agent picks the best and explains why.
 */
@Slf4j
@Service
public class MovieRecommendationService {

    private static final int MAX_CANDIDATES = 12;
    private static final int MAX_RECOMMENDATIONS = 5;

    private static final ParameterizedTypeReference<List<MovieCandidate>> CANDIDATE_LIST =
            new ParameterizedTypeReference<>() {};
    private static final ParameterizedTypeReference<List<MovieRecommendation>> RECOMMENDATION_LIST =
            new ParameterizedTypeReference<>() {};

    private final ChatClient chatClient;

    public MovieRecommendationService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    public List<MovieRecommendation> recommend(RecommendationRequest request) {
        List<MovieCandidate> candidates = propose(request);
        if (candidates.isEmpty()) {
            throw new MovieRecommendationException("Could not find any matching movies to recommend. Try rephrasing.");
        }
        return curate(request, candidates);
    }

    /** Proposer agent: suggest candidate movies from the model's knowledge. */
    private List<MovieCandidate> propose(RecommendationRequest request) {
        String seeds = request.liked().isEmpty() ? "(none provided)" : String.join(", ", request.liked());
        try {
            List<MovieCandidate> candidates = chatClient.prompt()
                    .system("""
                            You are a film expert. Suggest up to %d real movies that match the user's
                            request. For each, give the exact title, release year and a one-line plot
                            overview. Only include real movies you are confident exist. Offer variety.
                            Do not include the movies the user says they already watched.
                            """.formatted(MAX_CANDIDATES))
                    .user("User request: %s%nMovies they already liked: %s".formatted(request.request(), seeds))
                    .call()
                    .entity(CANDIDATE_LIST);
            return candidates == null ? List.of() : candidates;
        } catch (RuntimeException ex) {
            log.error("Proposer agent failed", ex);
            throw new MovieRecommendationException("Failed to gather candidate movies: " + ex.getMessage(), ex);
        }
    }

    /** Curator agent: pick the best candidates and explain why, using only the given list. */
    private List<MovieRecommendation> curate(RecommendationRequest request, List<MovieCandidate> candidates) {
        String candidateList = formatCandidates(candidates);
        try {
            List<MovieRecommendation> picks = chatClient.prompt()
                    .system("""
                            You are a movie recommender. Choose the %d best matches for the user's
                            request from the CANDIDATE MOVIES list. Rules:
                            - Only recommend movies from the candidate list. Never invent titles.
                            - Keep the title and year exactly as given in the list.
                            - For each pick, write one warm sentence on why they'll like it,
                              tailored to their request.
                            """.formatted(MAX_RECOMMENDATIONS))
                    .user("User request: %s%n%nCANDIDATE MOVIES:%n%s".formatted(request.request(), candidateList))
                    .call()
                    .entity(RECOMMENDATION_LIST);
            return picks == null ? List.of() : picks.stream().limit(MAX_RECOMMENDATIONS).toList();
        } catch (RuntimeException ex) {
            log.error("Curator agent failed", ex);
            throw new MovieRecommendationException("Failed to generate recommendations: " + ex.getMessage(), ex);
        }
    }

    private String formatCandidates(List<MovieCandidate> candidates) {
        return candidates.stream()
                .map(this::formatCandidate)
                .collect(Collectors.joining("\n"));
    }

    private String formatCandidate(MovieCandidate movie) {
        String year = movie.year() == null ? "?" : movie.year().toString();
        String overview = movie.overview() == null ? "" : movie.overview();
        if (overview.length() > 200) {
            overview = overview.substring(0, 200) + "...";
        }
        return "- %s (%s): %s".formatted(movie.title(), year, overview);
    }
}
