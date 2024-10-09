package com.example.TheMovieTracker.services;

import com.example.TheMovieTracker.dto.DiaryEntryDto;
import com.example.TheMovieTracker.entities.DiaryEntry;
import com.example.TheMovieTracker.repositories.DiaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DiaryService {

    @Autowired
    private DiaryRepository diaryRepository;

    public List<DiaryEntryDto> getDiaryEntriesForUser(Long userId) {
        List<DiaryEntry> diaries = diaryRepository.findByUserIdOrderByWatchedAtDesc(userId); // Fetch diary sorted by latest entries
        return diaries.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private DiaryEntryDto convertToDto(DiaryEntry diary) {
        DiaryEntryDto dto = new DiaryEntryDto();
        dto.setMovieTitle(diary.getMovie().getTitle());
        dto.setMoviePoster(diary.getMovie().getPosterUrl());
        dto.setReleaseDate(LocalDate.parse(diary.getMovie().getReleaseDate()));

        dto.setWatchedAt(Timestamp.valueOf(diary.getWatchedAt()));

        // Optional: Include review or rating if available
        if (diary.getReview() != null) {
            dto.setReviewContent(diary.getReview().getContent());
            dto.setRating(diary.getReview().getRating());
        }

        return dto;
    }
}
