package com.moviego.controller;

import com.moviego.dto.booking.ReservationRequest;
import com.moviego.service.BookingService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
@Tag(name = "영화 예매 API", description = "영화 예약")
public class BookingController {
    private final BookingService bookingService;
    /**
     * [1단계] 좌석 선택 완료 (임시 선점)
     * POST /api/bookings/temp
     */
    @PostMapping("/temp")
    public ResponseEntity<String> reserveTemporary(@RequestBody ReservationRequest request) {
        bookingService.reserveSeatsTemporarily(request);
        return ResponseEntity.ok("좌석이 10분간 선점되었습니다. 기간 내에 결제를 완료해주세요.");
    }

    /**
     * [2단계] 결제 완료 후 최종 예약 확정
     * POST /api/bookings/confirm
     */
    @PostMapping("/confirm")
    public ResponseEntity<String> confirmBooking(@RequestBody ReservationRequest request) {
        bookingService.completeBooking(request);
        return ResponseEntity.ok("예약이 최종 확정되었습니다.");
    }
}
