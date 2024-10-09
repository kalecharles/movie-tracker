package com.example.TheMovieTracker.controller;

import com.example.TheMovieTracker.dto.*;
import com.example.TheMovieTracker.entities.CustomList;
import com.example.TheMovieTracker.entities.ListEntry;
import com.example.TheMovieTracker.exceptions.NotFoundException;
import com.example.TheMovieTracker.services.CustomListService;
import com.example.TheMovieTracker.services.TmdbService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/lists")
public class CustomListController {

    @Autowired
    private CustomListService customListService;

    @Autowired
    private TmdbService tmdbService;

    @PostMapping("/create")
    public ResponseEntity<CustomListResponseDto> createList(@RequestBody CustomListRequestDto requestDto) throws NotFoundException {
        Long userId = tmdbService.getCurrentUser().getId();
        CustomList customList = customListService.createList(userId, requestDto.getListName(), requestDto.getDescription());
        CustomListResponseDto responseDto = new CustomListResponseDto(
          customList.getId(), customList.getListName(), customList.getDescription()
        );

        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/{listId}/add-movie")
    public ResponseEntity<CustomListWithEntriesResponseDto> addMovieToList(
            @PathVariable Long listId,
            @RequestBody AddMovieToListDto addMovieDto) throws NotFoundException {

        CustomList customList = customListService.addMovieToList(listId, addMovieDto.getMovieId(), addMovieDto.getMovieDescription()).getCustomList();
        List<MovieListEntryDto> movieListEntryDtos = customList.getEntries().stream()
                .map(entry -> new MovieListEntryDto(entry.getId(), entry.getMovie().getId(), entry.getMovie().getTitle(), entry.getMovieDescription()))
                .collect(Collectors.toList());

        CustomListWithEntriesResponseDto responseDto = new CustomListWithEntriesResponseDto(
                customList.getId(),
                customList.getListName(),
                customList.getDescription(),
                movieListEntryDtos
        );
        return ResponseEntity.ok(responseDto);
    }

    @PutMapping("/{listId}/update")
    public ResponseEntity<CustomListUpdateResponseDto> updateList(@PathVariable Long listId,
                                                            @RequestBody UpdateListEntryDto updateEntryDto) throws NotFoundException {
        CustomList customList = customListService.getListById(listId);
        customList.getEntries().forEach(entry -> System.out.println("Entry movie ID: " + entry.getMovie().getId()));
        System.out.println("Update entry: "+ updateEntryDto.getMovieId());
        ListEntry entryToUpdate = customList.getEntries().stream()
                .filter(entry -> entry.getMovie().getId().equals(updateEntryDto.getMovieId()))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Movie not found in the list"));

        entryToUpdate.setMovieDescription(updateEntryDto.getMovieDescription());
        customListService.save(customList);

        CustomListUpdateResponseDto responseDto = customListService.toResponseDto(customList);

        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/all")
    public ResponseEntity<List<CustomListUpdateResponseDto>> getAllLists() {
        List<CustomList> customLists = customListService.getAllCustomLists();

        List<CustomListUpdateResponseDto> responseDtos = customLists.stream()
                .map(customListService::toResponseDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseDtos);
    }


    @DeleteMapping("/{listId}")
    public ResponseEntity<String> deleteList(@PathVariable Long listId) {
        customListService.deleteList(listId);
        return ResponseEntity.ok("List deleted successfully");
    }

    @DeleteMapping("/{listId}/movies/{movieId}")
    public ResponseEntity<String> deleteMovieFromList(@PathVariable Long listId, @PathVariable Long movieId) {
        try{
            customListService.deleteMovieFromList(listId, movieId);
            return ResponseEntity.ok("Movie removed from the list successfully");
        }
        catch (NotFoundException e) {
            return ResponseEntity.ok(e.getLocalizedMessage() + "anta ra");
        }
    }

}
