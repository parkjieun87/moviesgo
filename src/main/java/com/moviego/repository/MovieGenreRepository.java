package com.moviego.repository;

import com.moviego.entity.MovieGenre;
import io.lettuce.core.dynamic.annotation.Param;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface MovieGenreRepository extends JpaRepository<MovieGenre, Long> {
    @Modifying // SELECT가 아닌 INSERT, UPDATE, DELETE 쿼리임을 명시
    @Transactional // 삭제 작업이 트랜잭션 내에서 실행되도록 보장
    @Query("DELETE FROM MovieGenre mg WHERE mg.movie.movieId = :movieId")
    void deleteByMovieId(@Param("movieId") Long movieId);
}
