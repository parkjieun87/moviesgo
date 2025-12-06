package com.moviego.dto.movie;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class TmdbResult {
    @JsonProperty("poster_path")
    private String posterPath;
    private String overview;
    private String title;
}
