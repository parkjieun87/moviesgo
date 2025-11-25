package com.moviego.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Bookings extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_id")
    private Long bookingId;

    @Column(name = "booking_number", nullable = false)
    private String bookingNumber;

    @Column(name = "total_price", nullable = false)
    private Integer totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "booking_status", length = 10)
    @Builder.Default
    private BookingStatus bookingStatus = BookingStatus.PENDING;

    @CreatedDate
    @Column(name = "booked_at", nullable=false, updatable=false)
    private LocalDateTime bookedAt;

    @LastModifiedDate
    @Column(name = "cancelledAt")
    private LocalDateTime cancelledAt;

    // 연관관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "screening_id", nullable = false)
    private Screenings screenings;

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL)
    @Builder.Default
    private List<BookingSeats> bookingSeats = new ArrayList<>();

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Payments> payments = new ArrayList<>();

    // 비즈니스 메서드
    public enum BookingStatus {
        PENDING, CONFIRMED, CANCELLED
    }
}
