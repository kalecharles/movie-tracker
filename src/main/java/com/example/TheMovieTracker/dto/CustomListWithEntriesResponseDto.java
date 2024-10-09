package com.example.TheMovieTracker.dto;

import lombok.Data;

import java.util.List;

@Data
public class CustomListWithEntriesResponseDto {

    private Long id;
    private String listName;
    private String description;
    private List<MovieListEntryDto> entries;

    public CustomListWithEntriesResponseDto(Long id, String listName, String description, List<MovieListEntryDto> entries) {
        this.id = id;
        this.listName = listName;
        this.description = description;
        this.entries = entries;
    }
}
