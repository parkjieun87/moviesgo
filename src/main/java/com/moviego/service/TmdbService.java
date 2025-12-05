package com.moviego.service;

import com.moviego.dto.movie.TmdbMovie;
import com.moviego.dto.movie.TmdbResult;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TmdbService {
    private final RestTemplate restTemplate = new RestTemplate();

    // TMDB API 키, 설정 파일에서 주입받아야 함
    @Value("${tmdb.api.key}")
    private String apiKey;

    // TMDB 검색 API URL (제목과 개봉 연도로 검색)
    private static final String TMDB_SEARCH_URL = "https://api.themoviedb.org/3/search/movie";
    // 포스터 이미지의 기본 URL (일반적으로 w500을 사용)
    private static final String TMDB_IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w500";

    /**
     * TMDB에서 영화 제목과 개봉일로 포스터 및 줄거리를 검색합니다.
     * @param title 영화 제목 (KOFIC API에서 획득)
     * @param openDt KOFIC 개봉일 (YYYYMMDD 형식)
     * @return 줄거리, 포스터 URL이 담긴 TmdbResult DTO (가장 일치하는 1개)
     */
    public Optional<TmdbResult> searchMovie(String title, String openDt) {
        String year = openDt.substring(0, 4); // 개봉 연도만 추출

        String url = TMDB_SEARCH_URL +
                "?api_key=" + apiKey +
                "&language=ko-KR" +
                "&query=" + title +
                "&year=" + year;

        try {
            TmdbMovie response = restTemplate.getForObject(url, TmdbMovie.class);

            // 검색 결과 중 가장 첫 번째 결과를 사용합니다.
            return Optional.ofNullable(response)
                    .flatMap(res -> res.getResults().stream().findFirst());

        } catch (Exception e) {
            System.err.println("TMDB API 호출 중 오류 발생 (Title: " + title + "): " + e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * 포스터 경로를 전체 URL로 변환합니다.
     */
    public String getFullPosterUrl(String posterPath) {
        if (posterPath == null) return null;
        return TMDB_IMAGE_BASE_URL + posterPath;
    }
}
