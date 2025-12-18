package com.moviego.service;

import com.moviego.dto.seat.SeatResponse;
import com.moviego.entity.Screenings;
import com.moviego.entity.Seats;
import com.moviego.repository.BookingRepository;
import com.moviego.repository.ScreeningRepository;
import com.moviego.repository.SeatsRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SeatService {
    private final ScreeningRepository screeningRepository;
    private final SeatsRepository seatsRepository;
    private final BookingRepository bookingRepository;

    @Transactional(readOnly = true)
    public List<SeatResponse> getSeatLayout(Long screeningId) {
        // 1. 상영 회차 조회
        Screenings screening = screeningRepository.findById(screeningId)
                .orElseThrow(() -> new EntityNotFoundException("상영 정보를 찾을 수 없습니다."));

        // 2. 해당 상영관의 모든 물리적 좌석 조회
        List<Seats> allSeats = seatsRepository.findByTheater_TheaterIdOrderBySeatRowAscSeatNumberAsc(
                screening.getTheater().getTheaterId());

        // 3. 현재 상영 회차에서 예약된 좌석 ID 목록을 Set으로 변환 (검색 속도 향상 O(1))
        java.util.Set<Long> reservedSeatIds = new java.util.HashSet<>(
                bookingRepository.findReservedSeatIds(screeningId)
        );

        // 4. 변환
        return allSeats.stream().map(seat -> {
            boolean isReserved = reservedSeatIds.contains(seat.getSeatId());
            return new SeatResponse(seat, isReserved);
        }).collect(Collectors.toList());
    }
}
