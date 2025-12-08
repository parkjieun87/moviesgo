package com.moviego.service;

import com.moviego.dto.movie.*;
import com.moviego.entity.Genres;
import com.moviego.entity.MovieGenre;
import com.moviego.entity.Movies;
import com.moviego.mapper.MovieMapper;
import com.moviego.repository.GenreRepository;
import com.moviego.repository.MovieGenreRepository;
import com.moviego.repository.MovieRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService {

    private final RestTemplate restTemplate = new  RestTemplate();
    private static final String MOVIE_INFO_API_URL = "http://www.kobis.or.kr/kobisopenapi/webservice/rest/movie/searchMovieInfo.json";
    private final MovieGenreRepository movieGenreRepository;
    private final GenreRepository genreRepository;
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

        // 1단계 해결: movieToProcess 변수를 선언하고 null로 초기화합니다.
        Movies movieToProcess = null;

        try {
            if (existingMovieOpt.isPresent()) {
                // 2. 존재하는 경우: 기존 엔티티를 업데이트
                movieToProcess = existingMovieOpt.get(); //movieToProcess에 할당 (업데이트 대상)

                // Mapper에 TMDB 데이터와 TmdbService를 함께 전달하여 업데이트
                movieMapper.updateEntity(movieInfo, movieToProcess, tmdbDataOpt, tmdbService);

                movieRepository.save(movieToProcess);

                System.out.println("🔄 영화 상세 정보 업데이트 완료: " + movieToProcess.getTitle() + " (" + koficMovieCd + ")");
            } else {
                // 3. 존재하지 않는 경우: 새로운 엔티티 생성 후 삽입
                // Mapper에 TMDB 데이터와 TmdbService를 함께 전달하여 새로운 엔티티 생성
                movieToProcess = movieMapper.toNewEntity(movieInfo, tmdbDataOpt, tmdbService); //movieToProcess에 할당 (신규 객체)

                movieRepository.save(movieToProcess); // INSERT 쿼리 실행

                System.out.println("✅ 신규 영화 상세 정보 저장 완료: " + movieToProcess.getTitle() + " (" + koficMovieCd + ")");
            }

            // 4단계: 장르 처리 로직을 if/else 블록 외부에서 호출합니다.
            // 이 시점에서 movieToProcess는 DB에 저장되어 ID를 가지거나, null이어야 합니다.
            if (movieToProcess.getMovieId() != null) {
                // DB에 저장된 ID와 KOFIC MovieInfo DTO를 전달합니다.
                processAndLinkGenres(movieToProcess.getMovieId(), movieInfo);
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

    private void processAndLinkGenres(Long movieId, MovieInfo movieInfo) {

        // 1. 장르 이름 리스트 확보
        List<String> localGenreNames = movieInfo.getKoficGenreNames();

        if (localGenreNames.isEmpty()) {
            movieGenreRepository.deleteByMovieId(movieId); // 관계만 삭제하고 종료
            return;
        }

        // 2. 기존 관계 초기화 (DELETE)
        movieGenreRepository.deleteByMovieId(movieId);

        // Movie 엔티티 참조 (MovieGenre 생성을 위한 외래키 설정)
        Movies movieReference = movieRepository.getReferenceById(movieId);
        List<MovieGenre> newRelations = new ArrayList<>();

        for (String genreName : localGenreNames) {

            // 3-1. 장르 Upsert: 이름으로 찾거나 새로 저장하여 ID 확보
            Genres genre = genreRepository.findByGenreName(genreName)
                    .orElseGet(() -> genreRepository.save(
                            Genres.builder().genreName(genreName).build()
                    ));

            // 3-2. 새로운 관계 엔티티 생성
            newRelations.add(MovieGenre.builder()
                    .movie(movieReference)
                    .genre(genre)
                    .build());
        }

        // 3-3. 새로운 관계 일괄 저장 (INSERT)
        movieGenreRepository.saveAll(newRelations);
    }

    /**
     * 페이지네이션을 적용하여 영화 목록을 조회합니다.
     * * @param page 조회할 페이지 번호 (0부터 시작)
     * @param size 페이지당 항목 수
     * @return 페이지 정보를 담은 영화 목록 (Page<Movie>)
     */
    public Page<MovieListResponse> getMovieList(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        // 1. DB에서 엔티티 Page를 가져옵니다.
        Page<Movies> moviePage = movieRepository.findAll(pageable);

        // 2. 엔티티 Page를 DTO Page로 변환합니다. (순환 참조 방지)
        // DTO의 생성자를 사용하여 Movies 엔티티에서 필요한 데이터만 추출
        return moviePage.map(MovieListResponse::new
        );
    }

    @Override
    public MovieDetailResponse getMovieDetail(Long movieId) {

        // 1. 로컬 DB 조회 (Movies 엔티티)
        Movies movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new EntityNotFoundException("Movie not found with id: " + movieId));

        // MovieGenre 연결 테이블을 거쳐 Genres 테이블의 장르 이름을 추출합니다.
        List<String> genreNames = movie.getMovieGenres().stream()
                .map(movieGenre -> movieGenre.getGenre().getGenreName()) // String을 추출
                .toList();
        // ----------------------------------------

        // 2. Kofic Movie Code 확보 및 API 호출 (기존 로직 유지)
        String koficCd = movie.getKoficMovieCd();
        MovieInfoResponse koficResponse = getMovieInfo(koficCd);
        MovieInfo movieInfoFromApi = koficResponse.getMovieInfoResult().getMovieInfo();

        // 3. DTO 변환 및 반환
        return new MovieDetailResponse(movie, genreNames, movieInfoFromApi);
    }
}
