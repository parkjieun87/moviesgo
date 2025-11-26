package com.moviego.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Seats extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seat_id")
    private Long seatId;

    @Column(name = "seat_row", nullable = false, length = 2)
    private String seatRow;

    @Column(name = "seat_number", nullable = false)
    private Integer seatNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "seat_type",length = 7)
    @Builder.Default
    private SeatType seatType = SeatType.NOMAL;

    @Column(name = "price_adjustment", nullable = false)
    @Builder.Default
    private Integer priceAdjustment=0;

    // 연관관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theater_id", nullable = false)
    private Theaters theater;

    @OneToMany(mappedBy = "seat", cascade = CascadeType.ALL)
    @Builder.Default
    private List<BookingSeats> bookingSeats = new ArrayList<>();

    @OneToMany(mappedBy = "seat", cascade = CascadeType.ALL)
    @Builder.Default
    private List<SeatReservations> seatReservations = new ArrayList<>();

    // 비즈니스 메서드
    public enum SeatType {
        NOMAL,
        VIP
    }
}
