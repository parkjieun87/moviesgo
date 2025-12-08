package com.moviego.dto.movie;

import com.moviego.entity.Movies;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class MovieDetailResponse {
    private Long movieId;
    private String movieNm;
    private String movieNmEn;
    private String prdtYear;
    private String fullDescription; // 전체 줄거리
    private String posterUrl;
    private LocalDate releaseDate; // 개봉일
    private String showTm;      // 상영 시간
    private String typeNm;      // 영화 유형 (ex. 장편)
    private String prdtStatNm;  // 제작 상태 (ex. 개봉)
    private String nationNm;    // 제작 국가
    private List<String> genres; // 장르 목록
    private List<MovieInfo.Audit> audits; // 관람 등급 목록

    public MovieDetailResponse(Movies movie, List<String> genreNames, MovieInfo apiData) {
        this.movieId = movie.getMovieId();
        this.fullDescription = movie.getDescription(); // Movies 엔티티의 description 필드를 매핑
        this.posterUrl = movie.getPosterUrl();
        this.releaseDate = movie.getReleaseDate();

        this.genres = genreNames;

        this.movieNm = apiData.getMovieNm();
        this.movieNmEn = apiData.getMovieNmEn();
        this.prdtYear = apiData.getPrdtYear();
        this.showTm = apiData.getShowTm();
        this.typeNm = apiData.getTypeNm();
        this.prdtStatNm = apiData.getPrdtStatNm();
        this.nationNm = apiData.getNationNm();
        this.audits = apiData.getAudits();
    }
}
