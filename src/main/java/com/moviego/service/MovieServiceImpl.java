package com.moviego.service;

import com.moviego.dto.movie.BoxOfficeMovie;
import com.moviego.dto.movie.MovieInfo;
import com.moviego.dto.movie.MovieInfoResponse;
import com.moviego.dto.movie.TmdbResult;
import com.moviego.entity.Movies;
import com.moviego.mapper.MovieMapper;
import com.moviego.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService {

    private final RestTemplate restTemplate = new  RestTemplate();
    private static final String MOVIE_INFO_API_URL = "http://www.kobis.or.kr/kobisopenapi/webservice/rest/movie/searchMovieInfo.json";
    @Value("${kofic.api.key}")
    private String apiKey;

    private final BoxOfficeService boxOfficeService;
    private final MovieMapper movieMapper;
    private final MovieRepository movieRepository;
    private final TmdbService tmdbService;

    @Override
    @Transactional
    public int saveDailyBoxOfficeMovies(String targetDt) {
        // 1. 일별 박스오피스 목록 조회 (movieCd 획득)
        List<BoxOfficeMovie> dailyBoxOfficeList = boxOfficeService.getDailyBoxOfficeList(targetDt);

        if (dailyBoxOfficeList.isEmpty()) {
            return 0;
        }

        int savedCount = 0;

        // 2. 각 영화의 상세 정보 조회 및 저장
        for (BoxOfficeMovie dailyMovie : dailyBoxOfficeList) {
            String movieCd = dailyMovie.getMovieCd();

            // 2-1. KOFIC 상세 정보 조회
            MovieInfoResponse koficResponse = getMovieInfo(movieCd);

            if (koficResponse != null && koficResponse.getMovieInfoResult() != null) {
                MovieInfo movieInfo = koficResponse.getMovieInfoResult().getMovieInfo();

                // ⭐ 2-2. TMDB 정보 추가 조회
                Optional<TmdbResult> tmdbDataOpt = tmdbService.searchMovie(
                        movieInfo.getMovieNm(),
                        movieInfo.getOpenDt()
                );

                // DB에 저장 로직 호출 (TMDB 데이터도 함께 전달)
                saveMovie(movieInfo, tmdbDataOpt);
                savedCount++;
            }
        }
        return savedCount;
    }

    /**
     * DB 저장 로직: KOFIC MovieCd를 기준으로 Upsert (Update or Insert) 처리
     * TMDB 데이터를 받도록 메서드 시그니처 변경
     */
    @Override
    @Transactional // 하나의 영화 저장/업데이트가 하나의 트랜잭션이 되도록 설정
    public void saveMovie(MovieInfo movieInfo, Optional<TmdbResult> tmdbDataOpt) {
        String koficMovieCd = movieInfo.getMovieCd();

        // 1. KOFIC MovieCd로 기존 엔티티 조회
        Optional<Movies> existingMovieOpt = movieRepository.findByKoficMovieCd(koficMovieCd);

        try {
            if (existingMovieOpt.isPresent()) {
                // 2. 존재하는 경우: 기존 엔티티를 업데이트
                Movies existingMovie = existingMovieOpt.get();

                // Mapper에 TMDB 데이터와 TmdbService를 함께 전달하여 업데이트
                movieMapper.updateEntity(movieInfo, existingMovie, tmdbDataOpt, tmdbService);

                movieRepository.save(existingMovie);

                System.out.println("🔄 영화 상세 정보 업데이트 완료: " + existingMovie.getTitle() + " (" + koficMovieCd + ")");
            } else {
                // 3. 존재하지 않는 경우: 새로운 엔티티 생성 후 삽입
                // Mapper에 TMDB 데이터와 TmdbService를 함께 전달하여 새로운 엔티티 생성
                Movies newMovie = movieMapper.toNewEntity(movieInfo, tmdbDataOpt, tmdbService);

                movieRepository.save(newMovie); // INSERT 쿼리 실행

                System.out.println("✅ 신규 영화 상세 정보 저장 완료: " + newMovie.getTitle() + " (" + koficMovieCd + ")");
            }

        } catch (Exception e) {
            System.err.println("❌ 영화 상세 정보 DB 저장 중 오류 발생 (MovieCd: " + koficMovieCd + "): " + e.getMessage());
        }
    }

    /**
     * KOFIC API에서 특정 영화 코드(movieCd)의 상세 정보를 조회합니다. (변경 없음)
     */
    public MovieInfoResponse getMovieInfo(String movieCd) {
        String url = MOVIE_INFO_API_URL + "?key=" + apiKey + "&movieCd=" + movieCd;

        try {
            return restTemplate.getForObject(url, MovieInfoResponse.class);
        } catch (Exception e) {
            System.err.println("영화 상세 정보 API 호출 중 오류 발생 (movieCd: " + movieCd + "): " + e.getMessage());
            return null;
        }
    }
}
