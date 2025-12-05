package com.moviego.controller;

import com.moviego.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
public class MovieController {
    
    private final MovieService movieService;

    @PostMapping("/daily-boxoffice")
    public ResponseEntity<String> saveDaliyBoxOffice(@RequestParam("targetDt") String targetDt) {
        if (targetDt == null || targetDt.length() != 8) {
            return ResponseEntity.badRequest().body("targetDt 파라미터는 YYYYMMDD 형식의 8자리여야 합니다.");
        }

        try {
            // Service 계층의 비즈니스 로직 호출
            int savedCount = movieService.saveDailyBoxOfficeMovies(targetDt);

            if (savedCount == 0) {
                // 데이터가 없거나 저장에 실패했지만, 요청 자체는 성공적으로 처리되었을 경우 200 OK를 반환할 수 있음
                // 하지만 여기서는 데이터 처리 실패로 간주하고 404/500 대신 메시지를 반환합니다.
                String message = targetDt + " 날짜의 박스오피스 데이터가 없거나 저장된 영화가 없습니다.";
                return ResponseEntity.status(HttpStatus.OK).body(message);
            }

            String successMessage = targetDt + " 날짜 박스오피스 영화 " + savedCount + "건의 상세 정보가 성공적으로 저장되었습니다. (KOFIC+TMDB)";
            return ResponseEntity.status(HttpStatus.OK).body(successMessage);

        } catch (Exception e) {
            // 예외 발생 시 500 Internal Server Error 반환
            System.err.println("데이터 저장 중 서버 오류 발생: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("❌ 데이터 저장 처리 중 심각한 오류가 발생했습니다.");
        }
    }
    
}
