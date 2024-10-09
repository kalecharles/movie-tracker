package com.example.TheMovieTracker.repositories;

import com.example.TheMovieTracker.entities.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByUserId(Long userId);

    List<Review> findByMovieId(Long movieId);

    Review findByUserIdAndMovieId(Long userId, Long movieId);
}
