package com.example.TheMovieTracker.controller;

import com.example.TheMovieTracker.dto.DiaryEntryDto;
import com.example.TheMovieTracker.services.DiaryService;
import com.example.TheMovieTracker.services.TmdbService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/diary")
public class DiaryController {

    @Autowired
    private DiaryService diaryService;

    @Autowired
    private TmdbService tmdbService;

    @GetMapping("/view")
    public ResponseEntity<List<DiaryEntryDto>> viewDiary() {
        Long currentUserId = tmdbService.getCurrentUser().getId();
        List<DiaryEntryDto> diaryEntries = diaryService.getDiaryEntriesForUser(currentUserId);
        return ResponseEntity.ok(diaryEntries);
    }
}
