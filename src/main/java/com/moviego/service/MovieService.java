package com.moviego.service;

import com.moviego.dto.movie.MovieInfo;
import com.moviego.dto.movie.MovieInfoResponse;
import com.moviego.dto.movie.TmdbResult;

import java.util.Optional;

public interface MovieService {
    // 박스오피스 영화 목록을 조회하고 상세 정보를 가져와 DB에 저장하는 핵심 메서드 (변경 없음)
    int saveDailyBoxOfficeMovies(String targetDt);

    // DB Entity로 변환하여 저장하는 메서드 (TMDB 정보를 받도록 시그니처 수정)
    void saveMovie(MovieInfo movieInfo, Optional<TmdbResult> tmdbDataOpt);

    // KOFIC 상세 정보를 조회하는 메서드 (변경 없음)
    MovieInfoResponse getMovieInfo(String movieCd);
}
