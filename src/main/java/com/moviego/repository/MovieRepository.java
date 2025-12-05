package com.moviego.repository;

import com.moviego.entity.Movies;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MovieRepository extends JpaRepository<Movies, Long> {
    Optional<Movies> findByKoficMovieCd(String koficMovieCd);
}
