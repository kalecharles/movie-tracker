package com.example.TheMovieTracker.dto;

import lombok.Data;

@Data
public class ListEntryResponseDto {
    private Long movieId;
    private String movieTitle;
    private String movieDescription;

    public ListEntryResponseDto(Long movieId, String movieTitle, String movieDescription) {
        this.movieId = movieId;
        this.movieTitle = movieTitle;
        this.movieDescription = movieDescription;
    }
}
