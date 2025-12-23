package com.moviego.controller;

import com.moviego.dto.seat.SeatResponse;
import com.moviego.service.SeatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/seat")
@RequiredArgsConstructor
@Tag(name = "좌석 선택 API", description = "좌석 관련 API")
public class SeatController {

    private final SeatService seatService;

    @Operation (summary = "상영 회차별 좌석 배치도 및 예약 현황 조회")
    @GetMapping("/screenings/{screeningId}/seats")
    public ResponseEntity<List<SeatResponse>> getSeats(@PathVariable Long screeningId) {
        return ResponseEntity.ok(seatService.getSeatLayout(screeningId));
    }
}
