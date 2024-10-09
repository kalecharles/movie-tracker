package com.example.TheMovieTracker.services;

import com.example.TheMovieTracker.dto.GenreDto;
import com.example.TheMovieTracker.dto.MovieDto;
import com.example.TheMovieTracker.entities.Favourite;
import com.example.TheMovieTracker.entities.Movie;
import com.example.TheMovieTracker.entities.User;
import com.example.TheMovieTracker.repositories.MovieRepository;
import com.example.TheMovieTracker.repositories.UserRepository;
import com.fasterxml.jackson.core.StreamWriteConstraints;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TmdbService {

    @Value("${tmdb.api.key}")
    private String apiKey;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MovieRepository movieRepository;

    private final String BASE_URL = "https://api.themoviedb.org/3";
    private final String IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w500";


    public User getCurrentUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String usernameOrEmail = authentication.getName(); // Get username or email from the security context

        // Find user by either username or email
        User user = userRepository.findByUsername(usernameOrEmail);
        if (user == null) {
            user = userRepository.findByEmail(usernameOrEmail);
        }
        return user;
    }

    public List<MovieDto> getFavourites() {
        User user = getCurrentUser(); // Retrieve the current logged-in user
        if (user != null) {
            return user.getFavourites().stream()   // Stream over the List<Favourite>
                    .map(favourite -> {
                        Movie movie = favourite.getMovie();
                        MovieDto movieDto = new MovieDto();
                        movieDto.setMovieId(movie.getTmdbId());
                        movieDto.setTitle(movie.getTitle());
                        movieDto.setOverview(movie.getOverview());
                        movieDto.setReleaseDate(movie.getReleaseDate());
                        movieDto.setPosterUrl(movie.getPosterUrl());
                        //convert comma separated genre string to list
                        movieDto.setGenres(Arrays.stream(movie.getGenres().split(","))
                                .map(genreName -> new GenreDto(genreName))
                                .collect(Collectors.toList()));
                        return movieDto;
                    })
                    .collect(Collectors.toList());
        }
        return null;
    }

    public List<MovieDto> searchMovie(String query) {
        String url = BASE_URL + "/search/movie?api_key=" + apiKey + "&query=" + query;
        String apiResponse = restTemplate.getForObject(url, String.class);

        ObjectMapper objectMapper = new ObjectMapper();
        List<MovieDto> movieDtoList = new ArrayList<>();
        try {
            JsonNode rootNode = objectMapper.readTree(apiResponse);
            JsonNode resultsNode = rootNode.path("results");
            if (resultsNode.isArray()) {
                for (JsonNode movieNode : resultsNode) {
                    MovieDto movieDto = new MovieDto();
                    movieDto.setMovieId(movieNode.path("id").asLong());
                    movieDto.setTitle(movieNode.path("title").asText());
                    movieDto.setOverview(movieNode.path("overview").asText());
                    movieDto.setReleaseDate(movieNode.path("release_date").asText());

                    String posterPath = movieNode.path("poster_path").asText(null);
                    if (posterPath != null) {
                        movieDto.setPosterUrl("https://image.tmdb.org/t/p/w500" + posterPath);
                    }

                    //movieDto.setGenres("N/A");

                    movieDtoList.add(movieDto);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return movieDtoList;
    }

    @Cacheable(value = "movies", key = "#movieId")
    public String getMovieDetails(Long movieId) {
        String url = BASE_URL + "/movie/" + movieId + "?api_key=" + apiKey;
        try {
            System.out.println("Calling TMDb API for the movie ID: " + movieId);
            String response = restTemplate.getForObject(url, String.class);
            System.out.println("TMDb API Response: " + response);
            return response;
        } catch (RestClientException e) {
            System.err.println("Error calling TMDb API: " + e.getMessage());
            return "Could not retrieve movie details. Please try again later.";
        }
    }

    @Cacheable(value = "popularMovies")
    public String getPopularMovies() {
        String url = BASE_URL + "/movie/popular?api_key=" + apiKey;
        return restTemplate.getForObject(url, String.class);
    }

    @Transactional
    public void addFavorite(Long movieId) {
        User user = getCurrentUser();
        Movie movie = movieRepository.findByTmdbId(movieId);
        if(movie == null){
            movie = new Movie();
            movie.setTmdbId(movieId);

            String movieDetails = getMovieDetails(movieId);
            movie.setTitle(extractTitleFromApiResponse(movieDetails));
            movie.setPosterUrl(extractPosterUrlFromApiResponse(movieDetails));

            // Save the new movie in the database
            movieRepository.save(movie);
        }

        Favourite favourite = new Favourite();
        favourite.setUser(user);
        favourite.setMovie(movie);
        userRepository.save(user);
    }

    private String extractPosterUrlFromApiResponse(String apiResponse) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.getFactory().setStreamWriteConstraints(StreamWriteConstraints.builder().maxNestingDepth(500).build());
        try{
            JsonNode rootNode = objectMapper.readTree(apiResponse);
            String posterPath = "https://image.tmdb.org/t/p/w500" + rootNode.path("poster_path").asText();
            return posterPath;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String extractTitleFromApiResponse(String apiResponse) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.getFactory().setStreamWriteConstraints(StreamWriteConstraints.builder().maxNestingDepth(500).build());
        try{
            JsonNode rootNode = objectMapper.readTree(apiResponse);
            return rootNode.path("title").asText();
        }
        catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }


    public Movie fetchMovieFromTmdb(Long movieId) {
        String tmdbUrl = "https://api.themoviedb.org/3/movie/" + movieId + "?api_key=" + apiKey;
        //MovieDto movieDto = restTemplate.getForObject(tmdbUrl, MovieDto.class);
        String apiResponse = restTemplate.getForObject(tmdbUrl, String.class);

        if (apiResponse != null) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                JsonNode rootNode = objectMapper.readTree(apiResponse);

                Movie movie = new Movie();
                movie.setTmdbId(movieId);

                movie.setId(rootNode.path("id").asLong());

                movie.setTitle(rootNode.path("title").asText());
                movie.setOverview(rootNode.path("overview").asText());
                movie.setReleaseDate(rootNode.path("release_date").asText());
                String posterPath = rootNode.path("poster_path").asText();
                movie.setPosterUrl("https://image.tmdb.org/t/p/w500" + posterPath);

                List<String> genreNames = rootNode.path("genres").findValuesAsText("name");
                movie.setGenres(String.join(",", genreNames));  // Join genres into a comma-separated string

                return movie;
            }
            catch (IOException e) {
                e.printStackTrace();
            }

        }
        return null;
    }
}
