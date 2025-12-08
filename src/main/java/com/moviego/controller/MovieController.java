package com.moviego.controller;

import com.moviego.dto.movie.MovieDetailResponse;
import com.moviego.dto.movie.MovieListResponse;
import com.moviego.service.MovieService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/movie")
@RequiredArgsConstructor
@Tag(name = "KOFIC 영화 API", description = "영화진흥위원회 API 일별 박스오피스 조회")
public class MovieController {
    
    private final MovieService movieService;
    // 1. 영화 조회해와서 movies 테이블에 저장 API
    @PostMapping("/daily-movie")
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

    // 2. 영화 목록 조회 API
    @GetMapping("/list")
    public ResponseEntity<Page<MovieListResponse>> getMovieList( // 반환 타입을 Page<Movie>로 명확히 지정
                                                      @RequestParam(value = "page", defaultValue = "0") int page,
                                                      @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        try {
            // ✅ Service 호출 결과를 DTO Page 타입으로 받습니다.
            Page<MovieListResponse> moviePage = movieService.getMovieList(page, size);

            if (moviePage.isEmpty()) {
                return ResponseEntity.status(HttpStatus.OK).body(moviePage);
            }

            return ResponseEntity.ok(moviePage);

        } catch (Exception e) {
            System.err.println("영화 목록 조회 중 서버 오류 발생: " + e.getMessage());

            // 오류 발생 시, 일관성을 위해 ResponseEntity<Page<MovieListResponse>>에 맞추어 null 대신
            // 빈 페이지 객체를 반환하거나, 에러 처리 DTO를 반환하는 것이 좋습니다.
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }
    
    // 3. 영화 상세 조회 API
    @GetMapping("/{movieId}") // ✅ 경로 변수 {movieId} 사용
    public ResponseEntity<MovieDetailResponse> getMovieDetail(
            @PathVariable("movieId") Long movieId // URL에서 ID 추출
    ) {
        try {
            // Service 메서드 호출 (DB + API 데이터 통합)
            MovieDetailResponse detail = movieService.getMovieDetail(movieId);

            return ResponseEntity.ok(detail);

        } catch (EntityNotFoundException e) {
            // DB에 해당 ID의 영화가 없을 경우 404 응답
            System.err.println("Movie not found: " + movieId);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            // KOFIC API 호출 실패 등 그 외 서버 오류 시 500 응답
            System.err.println("영화 상세 조회 중 서버 오류 발생: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}

