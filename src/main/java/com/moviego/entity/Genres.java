package com.moviego.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Genres extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "genre_id")
    private Long GenreId;

    @Column(name = "genre_name", nullable = false, length = 50)
    private String genreName;

    // 연관관계
    @OneToMany(mappedBy = "genre", cascade = CascadeType.ALL)
    @Builder.Default
    private List<UserPreferences> userPreferences = new ArrayList<>();

    @OneToMany(mappedBy = "genre", cascade = CascadeType.ALL)
    @Builder.Default
    private List<MovieGenre> movieGenres = new ArrayList<>();


}
