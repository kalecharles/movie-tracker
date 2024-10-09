package com.example.TheMovieTracker.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "movies")
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = true)
    private String posterUrl;

    @Column(nullable = false, unique = true)
    private Long tmdbId; // TMDB API Movie ID

    @OneToMany(mappedBy = "movie")
    private List<Review> reviews;

    // This maps to the Favourite entity, which links users and movies
    @OneToMany(mappedBy = "movie")
    @JsonManagedReference(value = "movie-favourites") // Use ManagedReference for the movie's favourites
    @JsonIgnore
    private List<Favourite> favourites;

    @Column(columnDefinition = "TEXT")
    private String overview;

    private String releaseDate;

    @Column(name = "genres")
    private String genres;
}
