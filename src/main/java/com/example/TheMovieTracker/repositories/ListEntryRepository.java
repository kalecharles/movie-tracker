package com.example.TheMovieTracker.repositories;

import com.example.TheMovieTracker.entities.ListEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ListEntryRepository extends JpaRepository<ListEntry, Long> {
}
