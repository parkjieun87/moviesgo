package com.moviego.service;

import com.moviego.dto.booking.ReservationRequest;
import com.moviego.entity.*;
import com.moviego.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final SeatReservationRepository seatReservationRepository;
    private final UserRepository userRepository;
    private final ScreeningRepository screeningRepository;
    private final SeatsRepository seatsRepository;

    @Transactional
    public void completeBooking(ReservationRequest request) {
        // 1. 중복 예약 체크
        if (bookingRepository.isSeatAlreadyReserved(request.screeningId(), request.seatIds())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미 예약된 좌석이 포함되어 있습니다.");
        }

        // 2. 기본 정보 조회
        Users user = userRepository.findById(request.userId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));
        Screenings screening = screeningRepository.findById(request.screeningId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "상영 정보를 찾을 수 없습니다."));

        // 3. 예매 번호 생성
        String bookingNumber = "BK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        // 4. 좌석 정보를 조회하며 금액 합산 및 예약 저장
        int calculatedTotalAmount = 0;

        for (Long seatId : request.seatIds()) {
            Seats seat = seatsRepository.findById(seatId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "좌석 정보 없음: " + seatId));

            int seatPrice = seat.getPriceAdjustment();
            calculatedTotalAmount += seatPrice;

            Bookings booking = Bookings.builder()
                    .bookingNumber(bookingNumber)
                    .totalPrice(seatPrice) // 각 좌석당 단가 저장
                    .bookingStatus(Bookings.BookingStatus.CONFIRMED)
                    .user(user)
                    .screening(screening)
                    .seat(seat)
                    .bookedAt(LocalDateTime.now())
                    .build();

            bookingRepository.save(booking);
        }

        // 5. 임시 점유 삭제
        seatReservationRepository.deleteByUserIdAndScreeningIdCustom(user.getUserId(), screening.getScreeningId());

        // 로직 확인용 로그 (필요시)
        System.out.println("총 결제 금액: " + calculatedTotalAmount + "원이 정상 처리되었습니다.");
    }
}
