package com.accenture.springai_bootcamp_demo.movie;

public class MovieRecommendationException extends RuntimeException {

    public MovieRecommendationException(String message) {
        super(message);
    }

    public MovieRecommendationException(String message, Throwable cause) {
        super(message, cause);
    }
}
