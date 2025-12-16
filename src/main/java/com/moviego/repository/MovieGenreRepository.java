package com.moviego.repository;

import com.moviego.entity.MovieGenre;
import com.moviego.entity.MovieGenreId;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MovieGenreRepository extends JpaRepository<MovieGenre, MovieGenreId> {
    @Modifying // SELECT가 아닌 INSERT, UPDATE, DELETE 쿼리임을 명시
    @Transactional // 삭제 작업이 트랜잭션 내에서 실행되도록 보장
    @Query("DELETE FROM MovieGenre mg WHERE mg.movie.movieId = :movieId")
    void deleteByMovieId(@Param("movieId") Long movieId);

    @Query("SELECT mg FROM MovieGenre mg JOIN FETCH mg.genre WHERE mg.movie.movieId = :movieId")
    List<MovieGenre> findByMovieId(@Param("movieId") Long movieId);
}
