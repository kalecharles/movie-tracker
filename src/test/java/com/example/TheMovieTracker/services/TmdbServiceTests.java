package com.example.TheMovieTracker.services;

import com.example.TheMovieTracker.dto.MovieDto;
import com.example.TheMovieTracker.entities.Movie;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class TmdbServiceTests {

    @Autowired
    private TmdbService tmdbService;

    @Test
    public void testGetMovieDetails() {
        Long randomMovieId = 50L + (long)(Math.random() * (300L - 50L));

        String movieResponse = tmdbService.getMovieDetails(randomMovieId);

        ObjectMapper objectMapper = new ObjectMapper();
        MovieDto movie = null;
        try{
            movie = objectMapper.readValue(movieResponse, MovieDto.class);
        }
        catch (JsonProcessingException e) {
            e.printStackTrace();
            fail("Failed to parse TMDb API response into Movie object");
        }

        System.out.println("Movie Details Fetched:");
        System.out.println("Movie ID: " + movie.getMovieId());
        System.out.println("Title: " + movie.getTitle());
        System.out.println("Poster URL: " + movie.getPosterUrl());
        System.out.println("Release Date: " + movie.getReleaseDate());
        System.out.println("Overview: " + movie.getOverview());
        System.out.println("Genres: " + movie.getGenres());

        assertNotNull(movie.getMovieId(), "Movie ID should not be null");
        assertNotNull(movie.getTitle(), "Title should not be null");
        assertFalse(movie.getTitle().isEmpty(), "Title should not be empty");

        assertNotNull(movie.getPosterUrl(), "Poster URL should not be null");
        assertFalse(movie.getPosterUrl().isEmpty(), "Poster URL should not be empty");

        assertNotNull(movie.getReleaseDate(), "Release date should not be null");
        assertFalse(movie.getReleaseDate().isEmpty(), "Release date should not be empty");

        assertNotNull(movie.getOverview(), "Overview should not be null");
        assertFalse(movie.getOverview().isEmpty(), "Overview should not be empty");

        assertNotNull(movie.getGenres(), "Genres should not be null");
        assertFalse(movie.getGenres().isEmpty(), "Genres list should not be empty");
    }
}
