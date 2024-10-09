package com.example.TheMovieTracker.controller;

import com.example.TheMovieTracker.dto.LoginRequest;
import com.example.TheMovieTracker.entities.User;
import com.example.TheMovieTracker.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;


    @GetMapping("/")
    public String greet() {
        return "Welcome to The Movie Tracker Application";
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User user) {
        try {
            if (userService.isUsernameTaken(user.getUsername())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already exists");
            }
            userService.register(user);
            return ResponseEntity.ok("User registered successfully");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred during registration");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody LoginRequest loginRequest) {
        boolean authenticated = userService.authenticate(loginRequest.getUsernameOrEmail(), loginRequest.getPassword());
        if (authenticated) {
            return ResponseEntity.ok("Login successful");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
    }

    @GetMapping("/oauth2/authorization/google")
    public ResponseEntity<String> oauth2Authorization() {
        return ResponseEntity.ok("OAuth2 authorization URL");
    }

    @GetMapping("/login?error")
    public String loginError(Model model) {
        model.addAttribute("loginError", true);
        return "login error page";  // Show login page with error message
    }

    @Bean
    public ApplicationRunner applicationRunner(ClientRegistrationRepository clientRegistrationRepository) {
        return args -> {
            ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId("google");
            if (clientRegistration != null) {
                System.out.println("OAuth2 Client Registration found: " + clientRegistration.getClientName());
            } else {
                System.out.println("No OAuth2 Client Registration named Google found!");
            }
        };
    }

//http://localhost:8080/oauth2/authorization/google
}
