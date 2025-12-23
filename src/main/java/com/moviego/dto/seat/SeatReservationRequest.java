package com.moviego.dto.seat;

import java.util.List;

public record SeatReservationRequest(
        Long userId,
        Long screeningId,
        List<Long> seatIds
) {}
