package com.example.TheMovieTracker.controller;

import com.example.TheMovieTracker.dto.MovieDto;
import com.example.TheMovieTracker.dto.MovieRequestDto;
import com.example.TheMovieTracker.entities.User;
import com.example.TheMovieTracker.entities.Watchlist;
import com.example.TheMovieTracker.repositories.WatchlistRepository;
import com.example.TheMovieTracker.services.TmdbService;
import com.example.TheMovieTracker.services.UserService;
import com.example.TheMovieTracker.services.WatchlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/watchlist")
public class WatchlistController {

    @Autowired
    private WatchlistService watchlistService;

    @Autowired
    private UserService userService;

    @Autowired
    private WatchlistRepository watchlistRepository;

    @Autowired
    private TmdbService tmdbService;

    @PostMapping("/add")
    public ResponseEntity<MovieDto> addMovieToWatchlist(@RequestBody MovieRequestDto movieRequest) {
        Long movieId = movieRequest.getMovieId();
        MovieDto movieDto = watchlistService.addToWatchlist(movieId);

        if(movieDto != null) {
            return new ResponseEntity<>(movieDto, HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping("/remove")
    public ResponseEntity<String> removeMovieFromWatchlist(@RequestBody MovieRequestDto movieRequest) {
        Long tmdbId = movieRequest.getMovieId();
        Long currentUserId = tmdbService.getCurrentUser().getId();

        String response = watchlistService.removeFromWatchlist(tmdbId, currentUserId);

        if(response.equals("Movie removed from watchlist successfully")) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        return new ResponseEntity<>(response, HttpStatus.I_AM_A_TEAPOT);
    }

    @GetMapping
    public ResponseEntity<List<MovieDto>> getWatchlist() {
//        List<MovieDto> watchlist = watchlistService.getWatchlist();
//        return new ResponseEntity<>(watchlist, HttpStatus.OK);
        User currentUser = userService.getCurrentUser();
        if(currentUser == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        List<Watchlist> watchlist = watchlistRepository.findByUser(currentUser);
        List<MovieDto> movieDtos = watchlist.stream()
                .map(w -> MovieDto.fromMovie(w.getMovie()))
                .collect(Collectors.toList());

        return new ResponseEntity<>(movieDtos, HttpStatus.OK);
    }
}
