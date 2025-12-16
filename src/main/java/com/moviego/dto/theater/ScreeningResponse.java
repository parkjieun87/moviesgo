package com.moviego.dto.theater;

import com.moviego.entity.Screenings;
import com.moviego.entity.Theaters;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScreeningResponse {
    private Long screeningId;        // 상영 정보 ID (예매 시 사용)
    private Long movieId;            // 영화 ID
    private String movieTitle;       // 영화 제목 (Screening 엔티티의 Movie에서 조회)

    private LocalTime startTime;     // 상영 시작 시간
    private LocalTime endTime;       // 상영 종료 시간

    private Theaters.ScreenType screenType;       // 상영관 타입 (예: "IMAX", "_2D")
    private int totalSeats;          // 상영관 총 좌석 수
    private int remainingSeats;      // 잔여 좌석 수 (Service에서 계산됨)
    private int basePrice;           // 기본 가격

    /**
     * Screening 엔티티와 계산된 잔여 좌석 수를 DTO로 변환하는 팩토리 메서드
     */
    public static ScreeningResponse from(Screenings screening, int remainingSeats) {
        return new ScreeningResponse(
                screening.getScreeningId(),
                screening.getMovie().getMovieId(),         // Movie 엔티티가 Screening에 연결되어 있다고 가정
                screening.getMovie().getTitle(),      // Movie 엔티티에서 제목 조회
                screening.getStartTime(),
                screening.getEndTime(),
                screening.getTheater().getScreenType(), // Theater 엔티티가 Screening에 연결되어 있다고 가정
                screening.getTotalSeats(),
                remainingSeats,
                screening.getBasePrice()
        );
    }       // 기본 가격 (Screenings 엔티티에서 조회 가능)
}
