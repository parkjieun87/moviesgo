package com.moviego.repository;

import com.moviego.entity.Bookings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface BookingRepository extends JpaRepository<Bookings, Long> {
    @Query("SELECT COUNT(b) FROM Bookings b WHERE b.screening.screeningId = :screeningId")
    int countByScreeningId(@Param("screeningId") Long screeningId);

    @Query("SELECT COUNT(b) > 0 FROM Bookings b " +
            "WHERE b.screening.screeningId = :screeningId " +
            "AND b.seat.seatId IN :seatIds " +
            "AND b.bookingStatus != 'CANCELLED'")
    boolean isSeatAlreadyReserved(@Param("screeningId") Long screeningId,
                                  @Param("seatIds") List<Long> seatIds);
}

