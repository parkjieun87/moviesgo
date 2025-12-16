package com.moviego.dto.theater;

import com.moviego.entity.Theaters;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TheaterInfo {
    private Long theaterId;       // 상영관 ID
    private String theaterName;   // 상영관 이름
    // private String screenType; // 필요시 추가

    /**
     * Theaters 엔티티를 받아서 TheaterInfo DTO를 생성하는 팩토리 메서드
     * @param theater Theaters 엔티티
     * @return TheaterInfo DTO
     */
    public static TheaterInfo from(Theaters theater) {
        return new TheaterInfo(
                theater.getTheaterId(),
                theater.getName()
        );
    }
}
