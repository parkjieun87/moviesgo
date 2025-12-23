package com.moviego.service;

import com.moviego.dto.booking.ReservationRequest;
import com.moviego.entity.*;
import com.moviego.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
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
    public void reserveSeatsTemporarily(ReservationRequest request) {
        LocalDateTime now = LocalDateTime.now();

        seatReservationRepository.deleteExpired(now); // 전체 만료 청소
        seatReservationRepository.deleteByUserIdAndScreeningId(request.userId(), request.screeningId()); // 내 기존 데이터 청소
        seatReservationRepository.flush(); // 즉시 반영

        // 1. [검증] 이미 최종 결제가 완료된 좌석인지 확인
        if (bookingRepository.isSeatAlreadyReserved(request.screeningId(), request.seatIds())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미 예매가 완료된 좌석입니다.");
        }

        // 2. [검증] 다른 사용자의 '유효한' 선점 여부 확인
        List<SeatReservations> activeOthers = seatReservationRepository
                .findActiveOtherReservations(request.screeningId(), request.seatIds());

        boolean isOccupiedByOther = activeOthers.stream()
                .anyMatch(s -> !s.getUser().getUserId().equals(request.userId()));

        if (isOccupiedByOther) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "다른 사용자가 선택 중인 좌석입니다.");
        }

        // 3. [로직] 기존 내 점유 데이터 삭제
        // 여기서 삭제를 진행해도 나중에 에러가 터지면 트랜잭션이 알아서 복구(롤백)해줍니다.
        seatReservationRepository.deleteByUserIdAndScreeningId(request.userId(), request.screeningId());

        // DELETE 쿼리를 즉시 실행하여 유니크 제약 조건 충돌을 방지함
        seatReservationRepository.flush();

        try {
            // 4. [로직] 새로운 점유 저장
            for (Long seatId : request.seatIds()) {
                // findById 대신 getReferenceById를 쓰면 쿼리를 줄일 수 있습니다 (성능 최적화)
                Seats seat = seatsRepository.getReferenceById(seatId);

                SeatReservations reservation = SeatReservations.builder()
                        .user(userRepository.getReferenceById(request.userId()))
                        .screening(screeningRepository.getReferenceById(request.screeningId()))
                        .seat(seat)
                        .expiresAt(now.plusMinutes(1))
                        .build();
                seatReservationRepository.save(reservation);
            }

            // 5. [핵심] DB에 즉시 반영하여 Unique 제약 조건 위반 여부 확인
            // 동시성 에러(동시 클릭)가 있다면 여기서 DataIntegrityViolationException이 터집니다.
            seatReservationRepository.flush();

        } catch (DataIntegrityViolationException e) {
            // DB 유니크 제약 조건(uk_screening_seat) 충돌 시 발생
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 다른 사용자가 선점한 좌석입니다.");
        }

    }

    @Transactional
    public void completeBooking(ReservationRequest request) {
        // 1. [핵심 추가] 다른 사람이 '유효하게' 점유 중인지 최종 확인
        // 다른 사람의 expiresAt이 아직 지나지 않은 선점 데이터가 있다면 결제 차단
        List<SeatReservations> activeOthers = seatReservationRepository
                .findActiveOtherReservations(request.screeningId(), request.seatIds());

        boolean isOccupiedByOther = activeOthers.stream()
                .anyMatch(s -> !s.getUser().getUserId().equals(request.userId()));

        if (isOccupiedByOther) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "다른 사용자가 결제 중인 좌석입니다.");
        }

        // 2. 최종 중복 예약 체크 (이미 결제 완료된 건이 있는지)
        if (bookingRepository.isSeatAlreadyReserved(request.screeningId(), request.seatIds())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미 예약 완료된 좌석입니다.");
        }

        // 3. 데이터 로드 (getReferenceById를 쓰면 쿼리 최적화가 됩니다)
        Users user = userRepository.getReferenceById(request.userId());
        Screenings screening = screeningRepository.getReferenceById(request.screeningId());
        String bookingNumber = "BK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        // 4. 확정 예약 저장
        for (Long seatId : request.seatIds()) {
            Seats seat = seatsRepository.findById(seatId).orElseThrow();

            Bookings booking = Bookings.builder()
                    .bookingNumber(bookingNumber)
                    .totalPrice(seat.getPriceAdjustment()) // 금액 계산 로직 필요 시 추가
                    .bookingStatus(Bookings.BookingStatus.CONFIRMED)
                    .user(user)
                    .screening(screening)
                    .seat(seat)
                    .bookedAt(LocalDateTime.now())
                    .build();

            bookingRepository.save(booking);
        }

        // 5. 결제 완료되었으므로 내 임시 점유 데이터만 즉시 삭제
        seatReservationRepository.deleteByUserIdAndScreeningId(user.getUserId(), screening.getScreeningId());
    }
}
