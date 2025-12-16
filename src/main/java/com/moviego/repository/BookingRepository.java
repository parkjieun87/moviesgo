package com.moviego.repository;

import com.moviego.entity.Bookings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookingRepository extends JpaRepository<Bookings, Long> {
    @Query("SELECT COUNT(b) FROM Bookings b WHERE b.screening.screeningId = :screeningId")
    int countByScreeningId(@Param("screeningId") Long screeningId);
}
