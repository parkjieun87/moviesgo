package com.moviego.controller;

import com.moviego.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
public class MovieController {
    
    private final MovieService movieService;

    @GetMapping("/daily-boxoffice-save")
    public String saveDailyBoxOffice(@RequestParam("targetDt") String targetDt) {
        int savedCount = movieService.saveDailyBoxOfficeMovies(targetDt);

        if (savedCount == 0) {
            return targetDt + " 날짜의 박스오피스 데이터가 없거나 저장에 실패했습니다.";
        }
        return "✨ " + targetDt + " 날짜 박스오피스 영화 " + savedCount + "건의 상세 정보가 성공적으로 저장되었습니다.";
    }
    
}
