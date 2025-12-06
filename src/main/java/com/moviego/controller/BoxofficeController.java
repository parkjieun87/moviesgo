package com.moviego.controller;

import com.moviego.dto.movie.BoxOfficeMovie;
import com.moviego.service.BoxOfficeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/kofic")
@RequiredArgsConstructor
@Tag(name = "KOFIC 영화 API", description = "영화진흥위원회 API 일별 박스오피스 조회")
public class BoxofficeController {

    private final BoxOfficeService boxOfficeService;

    /**
     * 일별 박스오피스 목록을 KOFIC API에서 조회하여 반환합니다.
     * GET /api/kofic/boxoffice/daily?targetDt=20231201
     *
     * @param targetDt 조회할 날짜 (YYYYMMDD)
     * @return 일별 박스오피스 영화 목록 (DTO)
     */
    @GetMapping("/boxoffice/daily")
    public ResponseEntity<List<BoxOfficeMovie>> getDailyBoxOffice(
            @RequestParam("targetDt") String targetDt) {

        List<BoxOfficeMovie> boxOfficeList = boxOfficeService.getDailyBoxOfficeList(targetDt);

        // 200 OK와 함께 목록 반환
        return ResponseEntity.ok(boxOfficeList);
    }
}
