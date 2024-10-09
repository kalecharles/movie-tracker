package com.example.TheMovieTracker.dto;

import lombok.Data;

@Data
public class UpdateListEntryDto {
    private Long movieId;
    private String movieDescription;
}
