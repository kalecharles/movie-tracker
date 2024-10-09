package com.example.TheMovieTracker.repositories;

import com.example.TheMovieTracker.entities.DiaryEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DiaryRepository extends JpaRepository<DiaryEntry, Long> {

    List<DiaryEntry> findByUserIdOrderByWatchedAtDesc(Long userId);

    DiaryEntry findByUserIdAndMovieId(Long userId, Long movieId);

    List<DiaryEntry> findByMovieId(Long movieId);

    boolean existsByUserIdAndMovieId(Long userId, Long movieId);
}
