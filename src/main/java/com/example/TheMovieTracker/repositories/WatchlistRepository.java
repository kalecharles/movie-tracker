package com.example.TheMovieTracker.repositories;

import com.example.TheMovieTracker.entities.User;
import com.example.TheMovieTracker.entities.Watchlist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WatchlistRepository extends JpaRepository<Watchlist, Long> {

    boolean existsByUserAndMovieId(User user, Long movieId);
    Watchlist findByUserAndMovieId(User user, Long movieId);
    List<Watchlist> findByUser(User user);
}
