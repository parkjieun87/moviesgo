package com.moviego.service;

import com.moviego.dto.movie.BoxOfficeMovie;
import com.moviego.dto.movie.MovieInfo;
import com.moviego.dto.movie.MovieInfoResponse;

import java.util.List;

public interface BoxOfficeService {
    /**
     * KOFIC API에서 특정 날짜의 일별 박스오피스 목록을 조회합니다.
     * @param targetDt 조회할 날짜 (YYYYMMDD 형식)
     * @return 일별 박스오피스 영화 목록 (List<BoxOfficeMovie>)
     */
    List<BoxOfficeMovie> getDailyBoxOfficeList(String targetDt);

}
