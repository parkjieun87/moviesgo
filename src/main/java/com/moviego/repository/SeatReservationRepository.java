package com.moviego.repository;

import com.moviego.entity.SeatReservations;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SeatReservationRepository extends JpaRepository<SeatReservations, Long> {
    @Modifying
    @Query("DELETE FROM SeatReservations s WHERE s.user.userId = :userId AND s.screening.screeningId = :screeningId")
    void deleteByUserIdAndScreeningIdCustom(@Param("userId") Long userId, @Param("screeningId") Long screeningId);
}
