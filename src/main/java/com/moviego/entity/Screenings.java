package com.moviego.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Screenings extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "screening_id")
    private Long screeningId;

    @Column(name = "screening_date", nullable = false)
    private Date screeningDate;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(name = "base_price", nullable = false)
    private Integer basePrice=12000;

    @Column(name = "total_seats", nullable = false)
    private Integer totalSeats;

    // 연관관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movies movie;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theater_id", nullable = false)
    private Theaters theater;

    @OneToMany(mappedBy = "screening", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Bookings> bookings = new ArrayList<>();

    @OneToMany(mappedBy = "screening", cascade = CascadeType.ALL)
    @Builder.Default
    private List<BookingSeats> bookingSeats = new ArrayList<>();

    @OneToMany(mappedBy = "screening", cascade = CascadeType.ALL)
    @Builder.Default
    private List<SeatReservations> seatReservations = new ArrayList<>();
}
