package com.moviego.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(MovieGenreId.class)
public class MovieGenre extends BaseEntity {

    // 연관관계 (복합 키의 첫 번째 구성 요소)
    @Id // ⭐ 2. 기본 키의 일부임을 명시합니다.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movies movie;

    // 연관관계 (복합 키의 두 번째 구성 요소)
    @Id // ⭐ 3. 기본 키의 일부임을 명시합니다.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "genre_id", nullable = false)
    private Genres genre;

}
