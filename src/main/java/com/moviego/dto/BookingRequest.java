package com.moviego.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingRequest {

    @NotNull(message = "사용자 ID는 필수입니다")
    private Long userId;

    @NotNull(message = "상영 ID는 필수입니다")
    private Long screeningId;

    @NotEmpty(message = "좌석을 선택해주세요")
    private List<Long> seatIds;

    private String paymentMethod; // CARD, TRANSFER, etc.
}