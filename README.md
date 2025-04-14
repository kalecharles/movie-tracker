
# The Movie Tracker Application

Welcome to The Movie Tracker application! This project allows users to discover, organize, and engage with movies.

## Overview

The Movie Tracker consists of a Spring Boot backend API and a Next.js frontend.

* **Backend (Spring Boot):** Provides the core logic, data storage, and API endpoints for user authentication, movie data retrieval, and managing user-specific content like custom lists and favorites.
* **Frontend (Next.js):** A user-friendly web interface that allows users to interact with the backend API to search for movies, manage their lists, mark favorites, and more.

## Backend API

The Spring Boot API provides the following key functionalities:

* **User Authentication:**
    * `/register`: Allows new users to create an account.
    * `/login`: Authenticates existing users and returns a token for subsequent requests.
    * `/oauth2/authorization/google`: Initiates the Google OAuth 2.0 login flow.
* **Custom List Management:**
    * `/lists/create` (POST): Creates a new custom movie list.
    * `/lists/all` (GET): Retrieves all custom lists created by the authenticated user.
    * `/lists/{listId}` (GET): Retrieves details of a specific custom list.
    * `/lists/{listId}` (DELETE): Deletes a specific custom list.
    * `/lists/{listId}/add-movie` (POST): Adds a movie to a specific list.
    * `/lists/{listId}/update` (PUT): Updates the description of a movie within a specific list.
    * `/lists/{listId}/movies/{movieId}` (DELETE): Removes a specific movie from a list.
* **Movie Data:**
    * `/search/movie?query={searchTerm}` (GET): Searches for movies based on a query.
    * `/movie/{movieId}` (GET): Retrieves detailed information about a specific movie.
    * `/movies/popular` (GET): Retrieves a list of popular movies.
* **Favorites:**
    * `/favorites/add/{movieId}` (POST): Adds a movie to the authenticated user's favorites.
    * `/favorites` (GET): Retrieves the authenticated user's favorite movies.
    * `/favorites/remove/{movieId}` (DELETE): Removes a movie from the authenticated user's favorites.

**Note:** All authenticated endpoints require a valid Bearer token in the `Authorization` header of the request.

## Frontend Application

The Next.js frontend provides the following features:

* **User Authentication:** Registration and login pages, including Google OAuth integration.
* **Custom List Management:** Pages to create, view, update, and delete custom movie lists, as well as add and remove movies from these lists.
* **Movie Discovery:** A search page to find movies and a details page to view information about specific movies.
* **Favorites:** A page to view and manage the user's favorite movies.
* **Popular Movies:** A page displaying a list of popular movies.

## Getting Started (for Developers)

This section would typically include instructions on how to set up and run both the backend and frontend applications. Since we've focused on the frontend, you might include instructions like:

1.  **Clone the repository:** `git clone [repository-url]`
2.  **Navigate to the frontend directory:** `cd movie-tracker-front`
3.  **Install dependencies:** `npm install` or `yarn install`
4.  **Set up environment variables:** Create a `.env.local` file and configure the `API_BASE_URL` to point to your backend API.
5.  **Run the development server:** `npm run dev` or `yarn dev`
6.  **Open your browser:** Navigate to `http://localhost:3000` (or the port specified in your setup).

## Contributing

If you'd like to contribute to this project, please follow these guidelines:

1.  Fork the repository.
2.  Create a new branch for your feature or bug fix.
3.  Make your changes and commit them with clear and concise messages.
4.  Push your changes to your fork.
5.  Submit a pull request to the main repository.

## License

[Your License Information Here]
