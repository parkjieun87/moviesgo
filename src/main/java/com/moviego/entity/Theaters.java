package com.moviego.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Theaters extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "theater_id")
    private Long theaterId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 50)
    private String region;

    @Column(name = "total_seats", nullable = false, length = 50)
    private String totalSeats;

    @Enumerated(EnumType.STRING)
    @Column(name = "screen_type", length = 5)
    @Builder.Default
    private ScreenType screenType = ScreenType._2D;

    // 연관관계
    @OneToMany(mappedBy = "theater", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Seats> seats = new ArrayList<>();

    @OneToMany(mappedBy = "theater", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Screenings> screenings = new ArrayList<>();

    // 비즈니스 메서드
    public enum ScreenType {
        _2D,
        _3D,
        IMAX
    }
}
