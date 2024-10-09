package com.example.TheMovieTracker.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "list_movies")
public class ListEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "list_id")
    @JsonIgnore
    private CustomList customList;

    @ManyToOne
    @JoinColumn(name = "movie_id")
    private Movie movie;

    private String movieDescription;
}
