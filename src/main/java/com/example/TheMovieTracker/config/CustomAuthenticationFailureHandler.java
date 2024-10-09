package com.example.TheMovieTracker.config;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private static final Logger logger = LoggerFactory.getLogger(CustomAuthenticationFailureHandler.class);

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        logger.error("Authentication failed: {}", exception.getMessage());
        logger.error("Exception details: ", exception);
        if (exception.getCause() != null) {
            logger.error("Cause: ", exception.getCause());
        }
        logger.error("Request details: URI={}, Query String={}", request.getRequestURI(), request.getQueryString());

        // You can add more logging here, such as request headers, if needed

        // Redirect to the error page
        response.sendRedirect("/login?error");
    }
}