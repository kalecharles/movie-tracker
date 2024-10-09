package com.example.TheMovieTracker.dto;

import lombok.Data;

@Data
public class AddMovieToListDto {
    private Long movieId;
    private String movieDescription;

    public AddMovieToListDto(Long movieId, String movieDescription) {
        this.movieId = movieId;
        this.movieDescription = movieDescription;
    }
}
