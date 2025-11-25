package com.moviego.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payments extends  BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long paymentId;

    @Column(nullable = false)
    private Integer amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", length = 10)
    @Builder.Default
    private PaymentMethod paymentMethod = PaymentMethod.CARD;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", length = 10)
    @Builder.Default
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    @Column(name = "pg_transaction_id", nullable = true, length = 100)
    private String pgTransactionId;

    @CreatedDate
    @Column(name = "paid_at", nullable=false)
    private LocalDateTime paidAt;

    @LastModifiedDate
    @Column(name = "refund_at")
    private LocalDateTime refundAt;

    // 연관관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Bookings booking;


    // 비즈니스 메서드
    public enum PaymentMethod {
        CARD, CASH
    }

    public enum PaymentStatus {
        PENDING, COMPLETED, FAILED, REFUNDED
    }

}
