package com.example.TheMovieTracker.repositories;

import com.example.TheMovieTracker.entities.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieRepository extends JpaRepository<Movie, Long> {
    Movie findByTmdbId(Long tmdbId);
}
