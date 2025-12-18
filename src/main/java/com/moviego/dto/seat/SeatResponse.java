package com.moviego.dto.seat;

import com.moviego.entity.Seats;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SeatResponse {
    private Long seatId;
    private String seatRow;       // A, B, C...
    private Integer seatNumber;    // 1, 2, 3...
    private String seatName;      // A1, A2...
    private String seatType;      // NORMAL, VIP
    private Integer priceAdjustment; // 좌석 추가 금액
    private boolean isReserved;   // 예약 여부

    // 엔티티와 예약여부를 인자로 받는 생성자 추가
    public SeatResponse(Seats seat, boolean isReserved) {
        this.seatId = seat.getSeatId();
        this.seatRow = seat.getSeatRow();
        this.seatNumber = seat.getSeatNumber();
        this.seatName = seat.getSeatRow() + seat.getSeatNumber();
        this.seatType = seat.getSeatType().name();
        this.priceAdjustment = seat.getPriceAdjustment();
        this.isReserved = isReserved;
    }
}
