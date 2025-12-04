package com.moviego.repository;

import com.moviego.entity.Movies;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieRepository extends JpaRepository<Movies, Long> {
}
