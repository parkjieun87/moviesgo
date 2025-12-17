package com.moviego.dto.booking;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record ReservationRequest (

        @NotNull(message = "사용자 ID는 필수입니다.")
        @Schema(description = "사용자 고유 번호", example = "1")
        Long userId,

        @NotNull(message = "상영 ID는 필수입니다.")
        @Schema(description = "상영 시간표 고유 번호", example = "1")
        Long screeningId,

        @NotNull(message = "좌석 ID 리스트는 필수입니다.")
        @Schema(description = "선택한 좌석 ID 목록", example = "[1,2]")
        List<Long> seatIds
) {}
