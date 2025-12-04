package com.moviego.dto.movie;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class BoxOfficeMovie {
    private String rnum;         // 순번 (번호)
    private String rank;         // 해당일자 박스오피스 순위
    private String rankInten;    // 전일 대비 순위의 증감분
    private String rankOldAndNew; // 랭킹 신규 진입 여부 ("NEW" 또는 "OLD")
    private String movieCd;      // 영화코드 (-> 상세 정보 조회에 사용될 핵심 필드)
    private String movieNm;      // 영화명(국문)
    private String openDt;       // 개봉일 (YYYYMMDD)
    private String salesAmt;     // 해당일 매출액
    private String salesShare;   // 해당일자 전체 매출액 대비 해당 영화 매출액의 비율
    private String salesInten;   // 전일 대비 매출액 증감분
    private String salesChange;  // 전일 대비 매출액 증감 비율
    private String salesAcc;     // 누적 매출액
    private String audiCnt;      // 해당일 관객수
    private String audiInten;    // 전일 대비 관객수 증감분
    private String audiChange;   // 전일 대비 관객수 증감 비율
    private String audiAcc;      // 누적 관객수
    private String scrnCnt;      // 해당일 상영 스크린 수
    private String showCnt;      // 해당일 상영 횟수
}
