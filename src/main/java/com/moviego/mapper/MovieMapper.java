package com.moviego.mapper;

import com.moviego.dto.movie.MovieInfo;
import com.moviego.dto.movie.TmdbResult;
import com.moviego.entity.Movies;
import com.moviego.service.TmdbService;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Component
public class MovieMapper {

    private static final DateTimeFormatter KOFIC_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

    /**
     * 신규 영화 정보 저장 시 DTO를 Entity로 변환합니다. (INSERT 시 사용)
     * 시그니처 변경: TmdbResult와 TmdbService 인수를 받도록 수정
     */
    public Movies toNewEntity(MovieInfo dto, Optional<TmdbResult> tmdbDataOpt, TmdbService tmdbService) {
        if (dto == null) {
            return null;
        }

        // TMDB 데이터 추출 및 URL 변환
        String description = tmdbDataOpt.map(TmdbResult::getOverview).orElse(null);
        String posterUrl = tmdbDataOpt.map(TmdbResult::getPosterPath)
                .map(tmdbService::getFullPosterUrl)
                .orElse(null);

        return Movies.builder()
                .koficMovieCd(dto.getMovieCd())
                .title(dto.getMovieNm())
                .description(description)
                .duration(parseDuration(dto.getShowTm()))
                .releaseDate(parseLocalDate(dto.getOpenDt()))
                .rating(parseRating(dto))
                .isShowing(isShowing(dto.getPrdtStatNm()))
                .posterUrl(posterUrl)
                .build();
    }

    /**
     * 이미 존재하는 영화의 상세 정보가 변경되었을 때, 기존 Entity를 업데이트합니다. (UPDATE 시 사용)
     * 시그니처 변경: TmdbResult와 TmdbService 인수를 받도록 수정
     */
    public void updateEntity(MovieInfo dto, Movies existingEntity, Optional<TmdbResult> tmdbDataOpt, TmdbService tmdbService) {
        if (dto == null || existingEntity == null) {
            return;
        }

        // TMDB 데이터 추출 및 URL 변환
        String description = tmdbDataOpt.map(TmdbResult::getOverview).orElse(null);
        String posterUrl = tmdbDataOpt.map(TmdbResult::getPosterPath)
                .map(tmdbService::getFullPosterUrl)
                .orElse(null);


        // 기존 엔티티의 PK(movieId)와 Unique Key(koficMovieCd)는 유지됩니다.

        // KOFIC 필드 업데이트
        existingEntity.setTitle(dto.getMovieNm());
        existingEntity.setDuration(parseDuration(dto.getShowTm()));
        existingEntity.setReleaseDate(parseLocalDate(dto.getOpenDt()));
        existingEntity.setRating(parseRating(dto));
        existingEntity.setShowing(isShowing(dto.getPrdtStatNm()));

        // TMDB 필드 업데이트: null이 아닌 경우에만 갱신
        if (description != null) {
            existingEntity.setDescription(description);
        }
        if (posterUrl != null) {
            existingEntity.setPosterUrl(posterUrl);
        }

    }

    private Integer parseDuration(String showTm) {
        try {
            return Integer.parseInt(showTm);
        } catch (NumberFormatException | NullPointerException e) {
            return 0;
        }
    }

    private LocalDate parseLocalDate(String openDt) {
        try {
            return LocalDate.parse(openDt, KOFIC_DATE_FORMAT);
        } catch (Exception e) {
            return LocalDate.now();
        }
    }

    private Movies.Rating parseRating(MovieInfo dto) {
        String gradeNm = Optional.ofNullable(dto.getAudits())
                .filter(list -> !list.isEmpty())
                .map(list -> list.get(0).getWatchGradeNm())
                .orElse("정보 없음");

        if (gradeNm.contains("청소년")) {
            return Movies.Rating._18;
        } else if (gradeNm.contains("15세")) {
            return Movies.Rating._15;
        } else if (gradeNm.contains("12세")) {
            return Movies.Rating._12;
        } else {
            return Movies.Rating.ALL;
        }
    }

    private boolean isShowing(String prdtStatNm) {
        if (prdtStatNm != null && (prdtStatNm.equals("개봉") || prdtStatNm.equals("기타"))) {
            return true;
        }
        return false;
    }
}
