package com.moviego.dto.theater;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegionTheaterGroup {
    private String regionName;              // 지역 이름 (UI 왼쪽 목록, 예: '서울')
    private int theaterCount;               // 해당 지역의 상영관 수 (예: 18)
    private List<TheaterInfo> theaters;      // 이 지역에 속한 상영관 목록 (UI 오른쪽 목록)
}
