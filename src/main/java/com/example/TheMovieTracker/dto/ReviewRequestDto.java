package com.example.TheMovieTracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRequestDto {
    private Long movieId;
    //private Long userId;
    private String content;
    private Integer rating;
}
