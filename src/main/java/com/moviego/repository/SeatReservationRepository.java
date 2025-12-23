package com.moviego.repository;

import com.moviego.entity.SeatReservations;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

public interface SeatReservationRepository extends JpaRepository<SeatReservations, Long> {
    // 1. 내 기존 점유 삭제
    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM SeatReservations s WHERE s.user.userId = :userId AND s.screening.screeningId = :screeningId")
    void deleteByUserIdAndScreeningId(@Param("userId") Long userId, @Param("screeningId") Long screeningId);

    // 2. 다른 사람이 점유 중인 '유효한' 데이터가 있는지 확인 (시간 조건 포함)
    @Query("SELECT s FROM SeatReservations s " +
            "WHERE s.screening.screeningId = :screeningId " +
            "AND s.seat.seatId IN :seatIds " +
            "AND s.expiresAt > CURRENT_TIMESTAMP ")
    List<SeatReservations> findActiveOtherReservations(@Param("screeningId") Long screeningId,
                                                       @Param("seatIds") List<Long> seatIds
                                                       );

    @Query("SELECT s.seat.seatId FROM SeatReservations s " +
            "WHERE s.screening.screeningId = :screeningId AND s.expiresAt > CURRENT_TIMESTAMP")
    List<Long> findActiveReservedSeatIds(@Param("screeningId") Long screeningId);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("DELETE FROM SeatReservations s WHERE s.expiresAt < :now")
    void deleteExpired(@Param("now") LocalDateTime now);
}
