package com.example.TheMovieTracker.services;

import com.example.TheMovieTracker.dto.MovieDto;
import com.example.TheMovieTracker.entities.Movie;
import com.example.TheMovieTracker.entities.User;
import com.example.TheMovieTracker.entities.Watchlist;
import com.example.TheMovieTracker.repositories.MovieRepository;
import com.example.TheMovieTracker.repositories.UserRepository;
import com.example.TheMovieTracker.repositories.WatchlistRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WatchlistService {

    @Autowired
    private WatchlistRepository watchlistRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private TmdbService tmdbService;

    @Value("${tmdb.api.key}")
    private String apiKey;


    @Autowired
    private RestTemplate restTemplate;

    public MovieDto addToWatchlist(Long movieId) {
        User currentUser = userService.getCurrentUser();
        if(currentUser == null) {
            throw new RuntimeException("User not found");
        }

        if(watchlistRepository.existsByUserAndMovieId(currentUser, movieId)) {
            throw new RuntimeException("Movie is already in the watchlist");
        }

        if(movieId == null) {
            throw new RuntimeException("Movie ID is null/invalid");
        }

        Movie movie = movieRepository.findByTmdbId(movieId);
        if(movie == null) {
            movie = fetchMovieFromTmdbApi(movieId);
            if(movie == null) {
                throw new RuntimeException("Movie not found in the TMDb API");
            }
            movieRepository.save(movie);
        }
        Watchlist watchlist = new Watchlist();
        watchlist.setUser(currentUser);
        watchlist.setMovie(movie);
        watchlistRepository.save(watchlist);
        return MovieDto.fromMovie(movie);

    }

    @Cacheable(value = "movies", key = "#movieId")
    public Movie fetchMovieFromTmdbApi(Long movieId) {
        String tmdbUrl = "https://api.themoviedb.org/3/movie/" + movieId + "?api_key=" + apiKey;
        String apiResponse = restTemplate.getForObject(tmdbUrl, String.class);

        if(apiResponse != null) {
            ObjectMapper objectMapper = new ObjectMapper();
            try{
                JsonNode rootNode = objectMapper.readTree(apiResponse);

                // Log the API response to check if poster_path and genres exist
                System.out.println("TMDb API Response: " + apiResponse);

                Movie movie = new Movie();
                movie.setTmdbId(movieId);
                movie.setTitle(rootNode.path("title").asText());
                movie.setOverview(rootNode.path("overview").asText());
                movie.setReleaseDate(rootNode.path("release_date").asText());

                String posterPath = rootNode.path("poster_path").asText(null);
                if (posterPath != null) {
                    movie.setPosterUrl("https://image.tmdb.org/t/p/w500" + posterPath);
                } else {
                    System.out.println("Poster path is missing in the API response.");
                }

                List<String> genreNames = rootNode.path("genres").findValuesAsText("name");
                if (!genreNames.isEmpty()) {
                    movie.setGenres(String.join(",", genreNames));  // Join genres into a comma-separated string
                } else {
                    System.out.println("Genres are missing in the API response.");
                }

                return movie;

            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
        //return tmdbService.fetchMovieFromTmdb(movieId);
    }

    public String removeFromWatchlist(Long tmdbId, Long userId) {
        User currentUser = userService.getCurrentUser();
        if(currentUser == null) {
            return "User not found";
        }

        //get the movie by tmdb id
        Movie movie = movieRepository.findByTmdbId(tmdbId);
        if(movie == null) {
            return "Movie not found in the local database";
        }

        Watchlist watchlistItem = watchlistRepository.findByUserAndMovieId(currentUser, movie.getId());
        if (watchlistItem == null) {
            return "Movie not found in watchlist";
        }

        watchlistRepository.delete(watchlistItem);
        return "Movie removed from watchlist successfully";
    }

    public List<MovieDto> getWatchlist() {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            return Collections.emptyList();
        }

        List<Watchlist> watchlistItems = watchlistRepository.findByUser(currentUser);
        return watchlistItems.stream()
                .map(item -> MovieDto.fromMovie(item.getMovie())
                ).collect(Collectors.toList());

    }
}
