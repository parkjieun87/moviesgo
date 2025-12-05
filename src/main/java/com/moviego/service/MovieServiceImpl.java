package com.moviego.service;

import com.moviego.dto.movie.BoxOfficeMovie;
import com.moviego.dto.movie.MovieInfo;
import com.moviego.dto.movie.MovieInfoResponse;
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
    // 영화 상세목록을 가져오는 API URL
    private static final String MOVIE_INFO_API_URL = "http://www.kobis.or.kr/kobisopenapi/webservice/rest/movie/searchMovieInfo.json";
    @Value("${kofic.api.key}")
    private String apiKey;

    private final BoxOfficeService boxOfficeService;
    private final MovieMapper movieMapper;
    private final MovieRepository movieRepository;

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

            // BoxOfficeServiceImpl 캐스팅은 BoxOfficeService 인터페이스에 getMovieInfo가 없을 때의 임시 방편입니다.
            // (실제 프로젝트에서는 인터페이스에 선언해야 합니다.)
            MovieInfoResponse response = getMovieInfo(movieCd);

            if (response != null && response.getMovieInfoResult() != null) {
                MovieInfo movieInfo = response.getMovieInfoResult().getMovieInfo();

                // DB에 저장 로직 호출
                saveMovie(movieInfo);
                savedCount++;
            }
        }
        return savedCount;
    }

    /**
     * DB 저장 로직: KOFIC MovieCd를 기준으로 Upsert (Update or Insert) 처리
     */
    @Override
    @Transactional // 하나의 영화 저장/업데이트가 하나의 트랜잭션이 되도록 설정
    public void saveMovie(MovieInfo movieInfo) {
        String koficMovieCd = movieInfo.getMovieCd();

        // 1. KOFIC MovieCd로 기존 엔티티 조회 (findByKoficMovieCd는 Repository에 추가되어야 함)
        Optional<Movies> existingMovieOpt = movieRepository.findByKoficMovieCd(koficMovieCd);

        try {
            if (existingMovieOpt.isPresent()) {
                // 2. 존재하는 경우: 기존 엔티티를 업데이트
                Movies existingMovie = existingMovieOpt.get();

                // Mapper를 통해 기존 엔티티에 새 정보를 반영 (updateEntity 메서드 필요)
                movieMapper.updateEntity(movieInfo, existingMovie);

                // save를 호출하여 변경된 내용을 DB에 반영 (UPDATE 쿼리 실행)
                movieRepository.save(existingMovie);

                System.out.println("🔄 영화 상세 정보 업데이트 완료: " + existingMovie.getTitle() + " (" + koficMovieCd + ")");
            } else {
                // 3. 존재하지 않는 경우: 새로운 엔티티 생성 후 삽입
                Movies newMovie = movieMapper.toNewEntity(movieInfo); // toNewEntity 메서드 필요

                movieRepository.save(newMovie); // INSERT 쿼리 실행

                System.out.println("✅ 신규 영화 상세 정보 저장 완료: " + newMovie.getTitle() + " (" + koficMovieCd + ")");
            }

        } catch (Exception e) {
            // 조회 후 저장을 하기 때문에 Unique Index 오류는 발생하지 않지만,
            // 다른 DB 오류 (예: 필드 길이 초과)가 발생할 수 있습니다.
            System.err.println("❌ 영화 상세 정보 DB 저장 중 오류 발생 (MovieCd: " + koficMovieCd + "): " + e.getMessage());
            // 트랜잭션이 롤백됩니다.
        }
    }

    /**
     * KOFIC API에서 특정 영화 코드(movieCd)의 상세 정보를 조회합니다.
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
