package com.example.TheMovieTracker.services;

import com.example.TheMovieTracker.entities.Favourite;
import com.example.TheMovieTracker.entities.Movie;
import com.example.TheMovieTracker.entities.User;
import com.example.TheMovieTracker.repositories.FavouriteRepository;
import com.example.TheMovieTracker.repositories.MovieRepository;
import com.example.TheMovieTracker.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class MovieService {

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FavouriteRepository favouriteRepository;

    @Autowired
    private TmdbService tmdbService;


    public String addToFavorites(Long movieId) {

        User currentUser = getCurrentUser();
        if(currentUser == null) {
            return "User not found";
        }
        List<Favourite> favoriteMovies = currentUser.getFavorites();

        if(favoriteMovies.size()>=4) {
            return "You have reached the maximum of 4 favorite movies.";
        }

        boolean alreadyFavorited = favoriteMovies.stream()
                .anyMatch( favourite -> favourite.getMovie().getTmdbId().equals(movieId));

        if(alreadyFavorited)
            return "This movie is already in the watchlist";

        Movie movieToAdd = movieRepository.findByTmdbId(movieId);
        if (movieToAdd == null) {
            movieToAdd = tmdbService.fetchMovieFromTmdb(movieId);
            if (movieToAdd == null) {
                return "Movie not found in TMDb.";
            }
            movieToAdd.setTmdbId(movieId);
            movieRepository.save(movieToAdd);
        }
        Favourite newFavourite = new Favourite();
        newFavourite.setMovie(movieToAdd);
        newFavourite.setUser(currentUser);

        favoriteMovies.add(newFavourite);
        favouriteRepository.save(newFavourite);

        return "Movie added to favorites successfully";
    }

    public User getCurrentUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String usernameOrEmail = authentication.getName(); // Get username or email from the security context

        // Find user by either username or email
        User user = userRepository.findByUsername(usernameOrEmail);
        if (user == null) {
            user = userRepository.findByEmail(usernameOrEmail);
        }
        return user;
    }

    @Transactional
    public String removeFromFavorites(Long movieId) {
        User currentUser = getCurrentUser();
        if(currentUser == null) {
            return "User not found";
        }
        List<Favourite> favoriteMovies = currentUser.getFavorites();
        Favourite movieToRemove = favoriteMovies.stream()
                .filter(favourite -> favourite.getMovie().getTmdbId().equals(movieId))
                .findFirst().orElse(null);

        if(movieToRemove == null) {
            return "Movie not found in favorites";
        }

        favoriteMovies.remove(movieToRemove); //from list
        favouriteRepository.delete(movieToRemove); //from database

        return "Movie removed from favorites successfully";
    }
}
