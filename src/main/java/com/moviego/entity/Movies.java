package com.moviego.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Movies extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "movie_id")
    private Long movieId;

    @Column(name = "kofic_movie_cd", nullable = false, length = 10, unique = true) // 🌟 Unique Key 설정
    private String koficMovieCd;

    @Column(nullable = false, length = 200)
    private String title;

    @Lob
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(length = 5)
    private Rating rating;

    @Column(nullable = false)
    private Integer duration;

    @Column(name = "poster_url", length = 500)
    private String posterUrl;

    @Column(name = "release_date", nullable = false)
    private LocalDate releaseDate;

    @Column(name = "is_showing", nullable = false)
    private boolean isShowing;

    // 연관관계
    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL)
    @Builder.Default
    private List<MovieGenre> movieGenres = new ArrayList<>();

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Screenings> screenings = new ArrayList<>();

    // 비즈니스 메서드
    public enum Rating {
        ALL,
        _12,
        _15,
        _18
    }
}
