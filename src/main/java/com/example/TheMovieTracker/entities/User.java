package com.example.TheMovieTracker.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = true, unique = true)
    private String email;

    @Column(nullable = false)
    private String username;

    private String password;


    private String oauth2Provider;
    private String oauth2ProviderId;


    // One watchlist per user
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "watchlist_id", referencedColumnName = "id")
    private Watchlist watchlist;

    // One user can have many reviews
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Review> reviews;

    // A many-to-many relationship for friends
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_friends",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "friend_id")
    )
        private List<User> friends;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DiaryEntry> diaryEntries;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MovieList> movieLists;

    // This maps to the Favourite entity, which links users and movies
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Favourite> favourites;

    public List<Favourite> getFavorites() {
        return favourites;
    }
}
