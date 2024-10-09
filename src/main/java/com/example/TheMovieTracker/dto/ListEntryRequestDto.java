package com.example.TheMovieTracker.dto;

import lombok.Data;

@Data
public class ListEntryRequestDto {
    private Long movieId;
    private String movieDescription;
}
