package com.example.TheMovieTracker.dto;

import lombok.Data;

@Data
public class GenreDto {
    private Long id;
    private String name;

    public GenreDto(String name) {
        this.name = name;
    }
}
