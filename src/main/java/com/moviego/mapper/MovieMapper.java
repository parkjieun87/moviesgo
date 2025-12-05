package com.moviego.mapper;

import com.moviego.dto.movie.MovieInfo;
import com.moviego.entity.Movies;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Component
public class MovieMapper {

    private static final DateTimeFormatter KOFIC_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

    /**
     * 신규 영화 정보 저장 시 DTO를 Entity로 변환합니다. (INSERT 시 사용)
     * @param dto MovieInfo DTO
     * @return Movies Entity (새로운 객체)
     */
    public Movies toNewEntity(MovieInfo dto) {
        if (dto == null) {
            return null;
        }

        // Movies.builder()를 사용하여 새로운 엔티티를 생성합니다.
        // 이 엔티티는 아직 DB에 저장되지 않았습니다.
        return Movies.builder()
                // 1. KOFIC 고유 코드 매핑 (Unique Index 컬럼)
                .koficMovieCd(dto.getMovieCd())

                // 2. 나머지 필드 매핑 및 가공
                .title(dto.getMovieNm())
                .description("")
                .duration(parseDuration(dto.getShowTm()))
                .releaseDate(parseLocalDate(dto.getOpenDt()))
                .rating(parseRating(dto))
                .isShowing(isShowing(dto.getPrdtStatNm()))

                // 연관관계 필드는 별도 로직으로 처리합니다.
                .build();
    }

    /**
     * 이미 존재하는 영화의 상세 정보가 변경되었을 때, 기존 Entity를 업데이트합니다. (UPDATE 시 사용)
     * @param dto 새로운 정보를 담은 MovieInfo DTO
     * @param existingEntity DB에서 조회된 기존 Movies Entity
     */
    public void updateEntity(MovieInfo dto, Movies existingEntity) {
        if (dto == null || existingEntity == null) {
            return;
        }

        // 기존 엔티티의 PK(movieId)와 Unique Key(koficMovieCd)는 유지됩니다.

        // 변경될 수 있는 필드만 setter를 사용하여 업데이트합니다.
        existingEntity.setTitle(dto.getMovieNm());
        existingEntity.setDescription("");
        existingEntity.setDuration(parseDuration(dto.getShowTm()));
        existingEntity.setReleaseDate(parseLocalDate(dto.getOpenDt()));
        existingEntity.setRating(parseRating(dto));
        existingEntity.setShowing(isShowing(dto.getPrdtStatNm()));

        // 포스터 URL, 장르 등 연관관계 필드 업데이트 로직이 있다면 여기에 추가해야 합니다.
        // 현재는 DTO에 없는 필드는 그대로 유지됩니다.
    }

    // --- 데이터 가공 보조 메서드 (변경 없음) ---

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
