package com.moviego.service;

import com.moviego.dto.movie.BoxOfficeMovie;
import com.moviego.dto.movie.BoxOfficeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service // <--- 여기에 @Service 어노테이션이 있어야 합니다.
@RequiredArgsConstructor
public class BoxOfficeServiceImpl implements BoxOfficeService {

    private final RestTemplate restTemplate = new  RestTemplate();
    // 일별 박스오피스 목록을 가져오는 API URL
    private static final String BOX_OFFICE_API_URL = "http://www.kobis.or.kr/kobisopenapi/webservice/rest/boxoffice/searchDailyBoxOfficeList.json";

    @Value("${kofic.api.key}")
    private String apiKey;

    /**
     * KOFIC API에서 특정 날짜의 일별 박스오피스 목록을 조회합니다.
     * @param targetDt 조회할 날짜 (YYYYMMDD 형식)
     * @return DailyBoxOfficeMovie 리스트
     */
    public List<BoxOfficeMovie> getDailyBoxOfficeList(String targetDt) {

        String url = BOX_OFFICE_API_URL +
                "?key=" + apiKey +
                "&targetDt=" + targetDt;

        try {
            // 1. API 호출: 최상위 DTO인 BoxOfficeResponse로 응답을 받습니다.
            BoxOfficeResponse response = restTemplate.getForObject(url, BoxOfficeResponse.class);

            // 2. 유효성 검사
            if (response == null ||
                    response.getBoxOfficeResult() == null ||
                    response.getBoxOfficeResult().getDailyBoxOfficeList() == null) {

                return List.of();
            }

            // 3. 실제 영화 목록을 반환
            return response.getBoxOfficeResult().getDailyBoxOfficeList();

        } catch (Exception e) {
            System.err.println("일별 박스오피스 API 호출 중 오류 발생: " + e.getMessage());
            return List.of();
        }
    }
}
