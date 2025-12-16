package com.moviego.repository;

import com.moviego.entity.Screenings;
import com.moviego.entity.Theaters;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ScreeningRepository extends JpaRepository<Screenings, Long> {
    // 1. 특정 영화 상영 극장 목록 조회 쿼리 (Movie ID 필드 확인)
    @Query("SELECT DISTINCT s.theater FROM Screenings s WHERE s.movie.movieId = :movieId")
    List<Theaters> findDistinctTheatersByMovieId(@Param("movieId") Long movieId);

    /**
     * 특정 영화와 극장에 해당하는 모든 상영 회차를 상영 시작 시간 오름차순으로 조회합니다.
     * (Screenings 엔티티가 movie와 theater 필드를 가지고 있다고 가정)
     */
//    List<Screenings> findByMovieIdAndTheaterIdOrderByStartTimeAsc(
//            Long movieId,
//            Long theaterId
//    );
}
