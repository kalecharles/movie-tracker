package com.example.TheMovieTracker.repositories;

import com.example.TheMovieTracker.entities.Favourite;
import com.example.TheMovieTracker.entities.Movie;
import com.example.TheMovieTracker.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FavouriteRepository extends JpaRepository<Favourite, Long> {
    List<Favourite> findByUser(User user);
    Favourite findByUserAndMovie(User user, Movie movie);
    void deleteByUserAndMovie(User user, Movie movie);
}
