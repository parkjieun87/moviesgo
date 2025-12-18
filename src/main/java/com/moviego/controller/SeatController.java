package com.moviego.controller;

import com.moviego.dto.seat.SeatResponse;
import com.moviego.service.SeatService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/seat")
@RequiredArgsConstructor
@Tag(name = "좌석 선택 API", description = "좌석 관련 API")
public class SeatController {

    private final SeatService seatService;

    @GetMapping("/screenings/{screeningId}/seats")
    public ResponseEntity<List<SeatResponse>> getSeats(@PathVariable Long screeningId) {
        return ResponseEntity.ok(seatService.getSeatLayout(screeningId));
    }
}
