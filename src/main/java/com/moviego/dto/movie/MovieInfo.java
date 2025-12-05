package com.moviego.dto.movie;

import lombok.Data;

import java.util.List;

@Data
public class MovieInfo {
    private String movieCd;     // 영화 코드 (Unique ID)
    private String movieNm;     // 영화명 (국문)
    private String movieNmEn;   // 영화명 (영문)
    private String prdtYear;    // 제작 연도
    private String openDt;      // 개봉일
    private String showTm;      // 상영 시간
    private String typeNm;      // 영화 유형 (ex. 장편)
    private String prdtStatNm;  // 제작 상태 (ex. 개봉)
    private String nationNm;    // 제작 국가
    private List<Genre> genres; // 장르 목록
    private List<Audit> audits; // 관람 등급 목록

    // 내부 클래스: 장르 정보
    @Data
    public static class Genre {
        private String genreNm; // 장르명
    }


    // 추가된 내부 클래스: 관람 등급 정보
    @Data
    public static class Audit {
        // 심의 번호 (사용하지 않을 경우 생략 가능)
        private String auditNo;
        // 관람 등급명 (예: 12세 이상 관람가, 전체 관람가)
        private String watchGradeNm;
    }
}
