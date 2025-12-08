package com.moviego.dto.movie;

import com.moviego.entity.Movies;
import lombok.Data;

import java.time.LocalDate;

@Data
public class MovieListResponse {

    private Long movieId;
    private String title;
    private Movies.Rating rating;
    private String posterUrl;
    private LocalDate releaseDate;

    public MovieListResponse(Movies movie) {
        this.movieId = movie.getMovieId();
        this.title = movie.getTitle();
        this.rating = movie.getRating();
        this.posterUrl = movie.getPosterUrl();
        this.releaseDate = movie.getReleaseDate();
    }
}
