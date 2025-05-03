package com.codeWithSrb.bookyourslot.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class CancelBookingInfoRequestDTO {

    @NotNull(message = "Booking Id is required")
    private int bookingId;

    @NotNull(message = "Date is required")
    private LocalDate date;

    @NotNull(message = "Start time is required")
    private LocalTime startTime;
}
