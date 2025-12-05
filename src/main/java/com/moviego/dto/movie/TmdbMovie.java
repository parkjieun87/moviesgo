package com.moviego.dto.movie;

import lombok.Data;

import java.util.List;

@Data
public class TmdbMovie {
    private List<TmdbResult> results;
}
