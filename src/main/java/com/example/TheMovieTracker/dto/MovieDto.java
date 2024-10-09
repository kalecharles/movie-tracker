package com.example.TheMovieTracker.dto;

import com.example.TheMovieTracker.entities.Movie;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MovieDto {
    private Long movieId;
    private String title;
    private String overview;

    @JsonProperty("release_date")
    private String releaseDate;

    @JsonProperty("name")
    private List<GenreDto> genres;

    @JsonProperty("poster_path")
    private String posterUrl;

    public static MovieDto fromMovie(Movie movie) {
        MovieDto dto = new MovieDto();
        dto.setMovieId(movie.getId());
        dto.setTitle(movie.getTitle());
        dto.setOverview(movie.getOverview());
        dto.setReleaseDate(movie.getReleaseDate());
        return dto;
    }
}
