package com.moviego.service;

import com.moviego.dto.theater.RegionTheaterGroup;
import com.moviego.dto.theater.TheaterInfo;
import com.moviego.dto.theater.TheaterResponse;
import com.moviego.entity.Theaters;
import com.moviego.repository.ScreeningRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ScreeningService {

    private final ScreeningRepository screeningRepository;

    public TheaterResponse getGroupedTheatersByMovieId(Long movieId) {

        // 1. 해당 영화를 상영하는 '고유한' Theater 엔티티 목록을 가져옵니다.
        //    (JPQL: SELECT DISTINCT s.theater FROM Screening s WHERE s.movie.movieId = :movieId)
        List<Theaters> theaters = screeningRepository.findDistinctTheatersByMovieId(movieId);

        // 2. 조회된 상영관 목록을 지역(Region)별로 그룹화합니다.
        //    Map<String, List<Theaters>> : Key = 지역 이름 (예: "서울"), Value = 해당 지역 상영관 엔티티 목록
        Map<String, List<Theaters>> groupedByRegion = theaters.stream()
                .collect(Collectors.groupingBy(Theaters::getRegion)); // 🎯 Theaters 엔티티에 getRegion() 메서드가 있다고 가정

        // 3. 그룹화된 Map을 RegionTheaterGroup DTO 리스트로 변환합니다.
        List<RegionTheaterGroup> regionGroups = groupedByRegion.entrySet().stream()
                .map(entry -> {
                    String regionName = entry.getKey();
                    List<Theaters> theaterList = entry.getValue();

                    // 3-1. 해당 지역의 상영관 엔티티 목록을 TheaterInfo DTO 목록으로 변환
                    List<TheaterInfo> theaterInfos = theaterList.stream()
                            .map(TheaterInfo::from) // 🎯 TheaterInfo.from(Theaters theater) 팩토리 메서드 필요
                            .collect(Collectors.toList());

                    // 3-2. RegionTheaterGroup DTO 생성
                    return new RegionTheaterGroup(
                            regionName,
                            theaterInfos.size(),
                            theaterInfos
                    );
                })
                .collect(Collectors.toList());

        // 4. 최종 응답 DTO에 담아 반환
        return new TheaterResponse(regionGroups);
    }
}
