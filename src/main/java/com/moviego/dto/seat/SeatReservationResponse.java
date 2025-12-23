package com.moviego.dto.seat;

import java.time.LocalDateTime;

public record SeatReservationResponse(
        String message,
        LocalDateTime expiresAt
) {}