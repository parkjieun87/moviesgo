package com.moviego.controller;

import com.moviego.dto.theater.TheaterResponse;
import com.moviego.service.ScreeningService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/screening")
@RequiredArgsConstructor
@Tag(name = "상영 일정 및 극장 API", description = "영화의 상영 극장, 시간표, 좌석 정보를 조회합니다.")
public class ScreeningController {

    private final ScreeningService screeningService;

    // 2. 특정 영화 상영 극장 목록 조회 API (GET /api/showtimes/theaters?movieId=1)
    @GetMapping("/theaters")
    public ResponseEntity<TheaterResponse> getTheatersByMovieId(
            @RequestParam("movieId") Long movieId) {

        // MovieController의 getTheatersByMovie 메서드에서 로직을 그대로 가져옵니다.
        TheaterResponse theaterResponse = screeningService.getGroupedTheatersByMovieId(movieId);
        return ResponseEntity.ok(theaterResponse);
    }


//    // 3. 특정 극장의 상영 시간표 및 좌석 조회 API (GET /api/showtimes/screenings?movieId=1&theaterId=1)
//    @GetMapping("/screenings")
//    public ResponseEntity<List<ShowtimeDetailResponse>> getScreeningsByMovieAndTheater(
//            @RequestParam("movieId") Long movieId,
//            @RequestParam("theaterId") Long theaterId) {
//
//        // MovieController의 getShowtimesWithSeats 메서드에서 로직을 그대로 가져옵니다.
//        List<ShowtimeDetailResponse> showtimes =
//                showtimeService.getShowtimesWithSeats(movieId, theaterId);
//
//        return ResponseEntity.ok(showtimes);
//    }

}
