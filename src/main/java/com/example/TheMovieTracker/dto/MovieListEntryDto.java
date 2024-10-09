package com.example.TheMovieTracker.dto;

import lombok.Data;

@Data
public class MovieListEntryDto {
    private Long id;
    private Long movieId;
    private String movieTitle;
    private String movieDescription;

    public MovieListEntryDto(Long id, Long movieId, String movieTitle, String movieDescription) {
        this.id = id;
        this.movieId = movieId;
        this.movieTitle = movieTitle;
        this.movieDescription = movieDescription;
    }
}
