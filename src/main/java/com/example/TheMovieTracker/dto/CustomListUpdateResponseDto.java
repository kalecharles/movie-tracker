package com.example.TheMovieTracker.dto;

import lombok.Data;

import java.util.List;

@Data
public class CustomListUpdateResponseDto {
    private Long id;
    private String listName;
    private String description;
    private List<ListEntryResponseDto> entries;

    public CustomListUpdateResponseDto(Long id, String listName, String description, List<ListEntryResponseDto> entries) {
        this.id = id;
        this.listName = listName;
        this.description = description;
        this.entries = entries;
    }
}
