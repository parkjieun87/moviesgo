package com.moviego.repository;

import com.moviego.entity.Screenings;
import com.moviego.entity.Theaters;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ScreeningRepository extends JpaRepository<Screenings, Long> {
    // 1. 특정 영화 상영 극장 목록 조회 쿼리 (Movie ID 필드 확인)
    @Query("SELECT DISTINCT s.theater FROM Screenings s WHERE s.movie.movieId = :movieId")
    List<Theaters> findDistinctTheatersByMovieId(@Param("movieId") Long movieId);

    @Query("SELECT s FROM Screenings s " +
            "WHERE s.movie.movieId = :movieId " +
            "AND s.theater.theaterId = :theaterId " +
            "AND s.screeningDate = :date " +
            "ORDER BY s.startTime ASC")
    List<Screenings> findShowtimes(
            @Param("movieId") Long movieId,
            @Param("theaterId") Long theaterId,
            @Param("date") LocalDate date
    );
}
