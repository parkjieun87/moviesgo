package com.moviego.dto.theater;

import com.moviego.entity.Theaters;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class  TheaterResponse {
    // 모든 지역 그룹의 리스트를 담아 한 번에 클라이언트로 보냅니다.
    private List<RegionTheaterGroup> regions;
}