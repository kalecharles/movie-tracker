package com.example.TheMovieTracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class TheMovieTrackerApplication {

	public static void main(String[] args) {
		SpringApplication.run(TheMovieTrackerApplication.class, args);
	}
}
