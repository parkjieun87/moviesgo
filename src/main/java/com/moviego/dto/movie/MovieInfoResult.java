package com.moviego.dto.movie;

import lombok.Data;

@Data
public class MovieInfoResult {
    private MovieInfo movieInfo; // 핵심 영화 정보
    private String source;
}
