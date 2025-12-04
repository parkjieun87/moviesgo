package com.moviego.dto.movie;

import lombok.Getter;
import lombok.ToString;

import java.util.List;
@Getter
@ToString
public class BoxOfficeResult {

        private String boxofficeType;
        private String showRange;

        // JSON의 "dailyBoxOfficeList" 키에 매핑됩니다.
        private List<BoxOfficeMovie> dailyBoxOfficeList;
}
