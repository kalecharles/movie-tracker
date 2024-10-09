package com.example.TheMovieTracker.services;

import com.example.TheMovieTracker.dto.CustomListResponseDto;
import com.example.TheMovieTracker.dto.CustomListUpdateResponseDto;
import com.example.TheMovieTracker.dto.ListEntryRequestDto;
import com.example.TheMovieTracker.dto.ListEntryResponseDto;
import com.example.TheMovieTracker.entities.CustomList;
import com.example.TheMovieTracker.entities.ListEntry;
import com.example.TheMovieTracker.entities.Movie;
import com.example.TheMovieTracker.exceptions.NotFoundException;
import com.example.TheMovieTracker.repositories.CustomListRepository;
import com.example.TheMovieTracker.repositories.ListEntryRepository;
import com.example.TheMovieTracker.repositories.MovieRepository;
import com.example.TheMovieTracker.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomListService {

    @Autowired
    private CustomListRepository customListRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private ListEntryRepository listEntryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TmdbService tmdbService;

    public CustomList createList(Long userId, String listName, String description) throws NotFoundException {

        CustomList customList = new CustomList();
        customList.setUser(userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found")));
        customList.setListName(listName);
        customList.setDescription(description);
        return customListRepository.save(customList);
    }


    public ListEntry addMovieToList(Long listId, Long movieId, String movieDescription) throws NotFoundException {

        CustomList customList = customListRepository.findById(listId)
                .orElseThrow(() -> new NotFoundException("List not found"));

        Movie movie = movieRepository.findByTmdbId(movieId);
        if(movie == null) {
            movie = tmdbService.fetchMovieFromTmdb(movieId);
            movieRepository.save(movie);
        }

        ListEntry listEntry = new ListEntry();
        listEntry.setCustomList(customList);
        listEntry.setMovie(movie);
        listEntry.setMovieDescription(movieDescription);
        return listEntryRepository.save(listEntry);

    }

    public CustomList updateList(Long listId, String listName, String description) throws NotFoundException {
        CustomList customList = customListRepository.findById(listId)
                .orElseThrow(() -> new NotFoundException("List not found"));

        customList.setListName(listName);
        customList.setDescription(description);
        return customListRepository.save(customList);
    }

    public void deleteList(Long listId) {
        customListRepository.deleteById(listId);
    }

    public void deleteMovieFromList(Long listId, Long movieId) throws NotFoundException {
        CustomList customList = getListById(listId);
        ListEntry entryToDelete = customList.getEntries().stream()
                .filter(entry -> entry.getMovie().getId().equals(movieId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Movie not found in the list"));
        customList.getEntries().remove(entryToDelete);
        customListRepository.save(customList);
    }

    public List<CustomList> getListsForUser(Long userId) {
        return customListRepository.findByUserId(userId);
    }

    public CustomList getListById(Long listId) throws NotFoundException {
        return customListRepository.findById(listId)
                .orElseThrow(() -> new NotFoundException("Custom List not found"));
    }

    public CustomList save(CustomList customList) {
        return customListRepository.save(customList);
    }

    public CustomListUpdateResponseDto toResponseDto(CustomList customList) {
        List<ListEntryResponseDto> entryDtos = customList.getEntries().stream()
                .map(entry -> new ListEntryResponseDto(
                        entry.getMovie().getId(),
                        entry.getMovie().getTitle(),
                        entry.getMovieDescription()
                )).collect(Collectors.toList());

        //map CustomList to CustomListResponseDto
        return new CustomListUpdateResponseDto(
                customList.getId(),
                customList.getListName(),
                customList.getDescription(),
                entryDtos
        );
    }

    public List<CustomList> getAllCustomLists() {
        return customListRepository.findAll();
    }
}
