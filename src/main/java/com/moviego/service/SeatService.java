package com.moviego.service;

import com.moviego.dto.seat.SeatResponse;
import com.moviego.entity.Screenings;
import com.moviego.entity.Seats;
import com.moviego.repository.BookingRepository;
import com.moviego.repository.ScreeningRepository;
import com.moviego.repository.SeatReservationRepository;
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
    private final SeatReservationRepository seatReservationRepository;

    @Transactional(readOnly = true)
    public List<SeatResponse> getSeatLayout(Long screeningId) {
        Screenings screening = screeningRepository.findById(screeningId)
                .orElseThrow(() -> new EntityNotFoundException("상영 정보를 찾을 수 없습니다."));

        List<Seats> allSeats = seatsRepository.findByTheater_TheaterIdOrderBySeatRowAscSeatNumberAsc(
                screening.getTheater().getTheaterId());

        // 2. 최종 결제 완료된 좌석 ID들
        java.util.Set<Long> reservedSeatIds = new java.util.HashSet<>(
                bookingRepository.findReservedSeatIds(screeningId)
        );

        // 3. [추가] 아직 만료되지 않은 '임시 선점' 좌석 ID들 추가
        java.util.Set<Long> tempReservedIds = new java.util.HashSet<>(
                seatReservationRepository.findActiveReservedSeatIds(screeningId)
        );

        // 4. 변환 (최종 예약 또는 임시 선점 중 하나라도 해당되면 reserved = true)
        return allSeats.stream().map(seat -> {
            boolean isReserved = reservedSeatIds.contains(seat.getSeatId())
                    || tempReservedIds.contains(seat.getSeatId()); // 여기서 합쳐줍니다.
            return new SeatResponse(seat, isReserved);
        }).collect(Collectors.toList());
    }
}
