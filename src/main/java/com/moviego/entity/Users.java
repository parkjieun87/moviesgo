package com.moviego.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "users",
        indexes = {
                @Index(name="idx_email", columnList = "email")
        }
)
@Getter
@Setter
@NoArgsConstructor
// 💡 이 코드를 추가하여 빌더가 모든 인자를 가진 생성자에 접근하도록 허용합니다.
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Builder
public class Users extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(length = 255)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    @Builder.Default
    private Status status = Status.ACTIVE;

    @Column(nullable = false, length = 50)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", length = 10)
    @Builder.Default
    private UserRole role = UserRole.USER;

    @Column(name = "birth_date", nullable = false)
    private String birthDate;


    // 연관관계
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Bookings> bookings = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @Builder.Default
    private List<SocialAccounts> socialAccounts = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @Builder.Default
    private List<UserPreferences> userPreferences = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @Builder.Default
    private List<SeatReservations> seatReservations = new ArrayList<>();

    // 비즈니스 메서드
    public enum Status {
        ACTIVE,
        DELETED
    }

    public enum UserRole {
        USER, ADMIN
    }
}