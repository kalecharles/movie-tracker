package com.example.TheMovieTracker.repositories;

import com.example.TheMovieTracker.entities.CustomList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomListRepository extends JpaRepository<CustomList, Long> {

    List<CustomList> findByUserId(Long userId);
}
