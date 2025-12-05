package com.moviego.service;

import com.moviego.dto.movie.MovieInfo;

public interface MovieService {
    // 박스오피스 영화 목록을 조회하고 상세 정보를 가져와 DB에 저장하는 핵심 메서드
    int saveDailyBoxOfficeMovies(String targetDt);

    // DB Entity로 변환하여 저장하는 메서드 (실제 DB 로직)
    void saveMovie(MovieInfo movieInfo);
}
