package com.example.TheMovieTracker.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.sql.Timestamp;
import java.time.LocalDate;

@Data
public class DiaryEntryDto {
    private String movieTitle;
    private String moviePoster;
    private LocalDate releaseDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy") // Format it when serializing to JSON
    private Timestamp watchedAt;
    private String reviewContent;
    private Integer rating;
}
