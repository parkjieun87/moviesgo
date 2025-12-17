package com.moviego.controller;

import com.moviego.dto.theater.ScreeningResponse;
import com.moviego.dto.theater.TheaterResponse;
import com.moviego.service.ScreeningService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/screening")
@RequiredArgsConstructor
@Tag(name = "상영 일정 및 극장 API", description = "영화의 상영 극장, 시간표, 좌석 정보를 조회합니다.")
public class ScreeningController {

    private final ScreeningService screeningService;

    // 1. 특정 영화 상영 극장 목록 조회 API (GET /api/screening/theaters?movieId=1)
    @GetMapping("/theaters")
    public ResponseEntity<TheaterResponse> getTheatersByMovieId(
            @RequestParam("movieId") Long movieId) {

        // MovieController의 getTheatersByMovie 메서드에서 로직을 그대로 가져옵니다.
        TheaterResponse theaterResponse = screeningService.getGroupedTheatersByMovieId(movieId);
        return ResponseEntity.ok(theaterResponse);
    }


    // 2. 특정 극장의 상영 시간표 및 좌석 조회 API (GET /api/screening/seats?movieId=1&theaterId=1)
    @GetMapping("/seats")
    public ResponseEntity<List<ScreeningResponse>> getSeatsByTheater(
            @RequestParam("movieId") Long movieId,
            @RequestParam("theaterId") Long theaterId,
            @RequestParam("date") @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) LocalDate date) {

        // 1. Service 메서드 호출
        List<ScreeningResponse> showtime = screeningService.getShowtimesByTheater(movieId, theaterId, date);

        // 2. 결과 반환 (결과가 없을 경우 빈 리스트 반환)
        if (showtime.isEmpty()) {
            // 상영 일정이 없더라도 404 대신 빈 리스트(200 OK)를 반환하여 클라이언트 처리를 용이하게 함
            return ResponseEntity.ok(showtime);
        }

        return ResponseEntity.ok(showtime);
    }

}
