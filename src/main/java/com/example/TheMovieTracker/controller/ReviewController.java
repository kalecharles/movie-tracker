package com.example.TheMovieTracker.controller;

import com.example.TheMovieTracker.dto.ReviewRequestDto;
import com.example.TheMovieTracker.exceptions.NotFoundException;
import com.example.TheMovieTracker.services.ReviewService;
import com.example.TheMovieTracker.services.TmdbService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private TmdbService tmdbService;

    @PostMapping("/seen")
    public ResponseEntity<String> markAsSeenAndReview(@RequestBody ReviewRequestDto reviewRequestDto) throws NotFoundException {
        Long currentUserId = tmdbService.getCurrentUser().getId();

        boolean hasReviewContent = reviewRequestDto.getContent() != null && !reviewRequestDto.getContent().isEmpty();
        boolean hasRating = reviewRequestDto.getRating() != null;
        String response;
        if (!hasReviewContent && !hasRating) {
            response = reviewService.markAsWatched(reviewRequestDto.getMovieId(), currentUserId);
        } else {
            // If review or rating is provided, proceed with review and mark as watched
            response = reviewService.addReviewAndMarkWatched(
                    reviewRequestDto.getMovieId(),
                    currentUserId,
                    reviewRequestDto.getContent(),
                    hasRating ? reviewRequestDto.getRating() : 0
            );
        }
        return ResponseEntity.ok(response);
    }
}
