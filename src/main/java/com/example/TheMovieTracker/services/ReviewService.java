package com.example.TheMovieTracker.services;

import com.example.TheMovieTracker.entities.*;
import com.example.TheMovieTracker.exceptions.NotFoundException;
import com.example.TheMovieTracker.repositories.DiaryRepository;
import com.example.TheMovieTracker.repositories.MovieRepository;
import com.example.TheMovieTracker.repositories.ReviewRepository;
import com.example.TheMovieTracker.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Optional;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private DiaryRepository diaryRepository;

    @Autowired
    private WatchlistService watchlistService;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TmdbService tmdbService;


    public String addReviewAndMarkWatched(Long movieId, Long userId, String content, int rating) throws NotFoundException {

        Movie movie = movieRepository.findByTmdbId(movieId);
        if (movie == null) {
            movie = tmdbService.fetchMovieFromTmdb(movieId);
            if (movie == null) {
                throw new NotFoundException("Movie not found in TMDb API.");
            }
            movieRepository.save(movie);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Optional<DiaryEntry> diaryOpt = Optional.ofNullable(diaryRepository.findByUserIdAndMovieId(userId, movieId));
        DiaryEntry diary;
        if(diaryOpt.isPresent()) {
            diary = diaryOpt.get();
        }
        else {
            diary = new DiaryEntry();
            diary.setMovie(movie);
            diary.setUser(user);
            diary.setWatched(true);
            diary.setWatchedAt(new Timestamp(System.currentTimeMillis()).toLocalDateTime());
        }

        if(content != null && !content.isEmpty()) {
            Review review = new Review();
            review.setContent(content);
            review.setRating(rating);
            review.setUser(user);
            review.setMovie(movie);

            reviewRepository.save(review);

            diary.setReview(review);
        }
        diaryRepository.save(diary);
        watchlistService.removeFromWatchlist(movieId, userId);

        return "Review added and movie marked as watched";
    }

    public String markAsWatched(Long movieId, Long currentUserId) throws NotFoundException {

        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (currentUser == null) {
            return "User not found";
        }

        // Fetch the movie by its TMDB ID
        Movie movie = movieRepository.findByTmdbId(movieId);
        if (movie == null) {
            // If the movie is not in the local database, fetch it from the TMDb API
            movie = tmdbService.fetchMovieFromTmdb(movieId);
            if (movie == null) {
                return "Movie not found";
            }
            // Save the movie to the local database if it was fetched from TMDb
            movieRepository.save(movie);
        }

        // Check if the movie is already marked as watched in the user's diary
        DiaryEntry existingEntry = diaryRepository.findByUserIdAndMovieId(currentUserId, movieId);
        if (existingEntry != null) {
            return "Movie already marked as watched";
        }

        DiaryEntry diaryEntry = new DiaryEntry();
        diaryEntry.setUser(currentUser);
        diaryEntry.setMovie(movie);
        diaryEntry.setWatchedAt(LocalDate.now().atStartOfDay()); // Mark as watched today
        diaryRepository.save(diaryEntry);
        System.out.println("Diary entry created for movie ID: " + movieId + " and user ID: " + currentUserId);
        watchlistService.removeFromWatchlist(movieId, currentUserId);

        return "Movie marked as watched successfully";

    }
}
