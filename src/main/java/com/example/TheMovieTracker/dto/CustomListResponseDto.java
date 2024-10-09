package com.example.TheMovieTracker.dto;

import lombok.Data;

import java.util.List;

@Data
public class CustomListResponseDto {

    private Long id;
    private String listName;
    private String description;
    //private List<ListEntryResponseDto> entries;

    public CustomListResponseDto(Long id, String listName, String description) {
        this.id = id;
        this.listName = listName;
        this.description = description;
        //this.entries = entries;
    }
}
