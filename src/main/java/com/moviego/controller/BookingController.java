package com.moviego.controller;

import com.moviego.dto.booking.ReservationRequest;
import com.moviego.service.BookingService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
@Tag(name = "영화 예매 API", description = "영화 예약")
public class BookingController {
    private final BookingService bookingService;
    // 결제 없이 바로 예매를 생성하는 API
    @PostMapping("/create")
    public ResponseEntity<String> createBooking(@Valid @RequestBody ReservationRequest request) {
        bookingService.completeBooking(request);
        return ResponseEntity.status(HttpStatus.CREATED).body("예매가 완료되었습니다.");
    }
}
