package com.example.TheMovieTracker.controller;

import com.example.TheMovieTracker.dto.MovieDto;
import com.example.TheMovieTracker.entities.Movie;
import com.example.TheMovieTracker.services.MovieService;
import com.example.TheMovieTracker.services.TmdbService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/movie")
public class MovieController {

    @Autowired
    private TmdbService tmdbService;

    @Autowired
    private MovieService movieService;

    @GetMapping("/search")
    public ResponseEntity<List<MovieDto>> searchMovies(@RequestParam("query") String query) {
        System.out.println("Received search request for query: " + query);
        List<MovieDto> searchResults = tmdbService.searchMovie(query);
        if (searchResults.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return ResponseEntity.ok(tmdbService.searchMovie(query));
    }

    @GetMapping("/{id}")
    public ResponseEntity<String> getMovieDetails(@PathVariable Long id) {
        return ResponseEntity.ok(tmdbService.getMovieDetails(id));
    }

    @GetMapping("/popular")
    public ResponseEntity<String> getPopularMovies() {
        return ResponseEntity.ok(tmdbService.getPopularMovies());
    }

    @PostMapping("/favorites")
    public ResponseEntity<String> addFavorite(@RequestBody MovieDto movieDto) {
        Long movieId = movieDto.getMovieId();
        String response = movieService.addToFavorites(movieId);
        if(response.equals("Movie added to favorites successfully")) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/favorites")
    public ResponseEntity<List<MovieDto>> getFavorites() {
        List<MovieDto> favorites = tmdbService.getFavourites(); // Call the service method
        if (favorites != null) {
            return ResponseEntity.ok(favorites);  // Return the list of favorite movies
        }
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/favorites")
    public ResponseEntity<String> removeFavorite(@RequestBody Map<String, Long> payload) {
        Long movieId = payload.get("movieId");
        String response = movieService.removeFromFavorites(movieId);

        if(response.equals("Movie removed from favorites successfully")){
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
