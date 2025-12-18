package com.moviego.repository;

import com.moviego.entity.Seats;
import com.moviego.entity.Theaters;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeatsRepository extends JpaRepository<Seats, Long> {
    List<Seats> findByTheater_TheaterIdOrderBySeatRowAscSeatNumberAsc(Long theaterId);
}
