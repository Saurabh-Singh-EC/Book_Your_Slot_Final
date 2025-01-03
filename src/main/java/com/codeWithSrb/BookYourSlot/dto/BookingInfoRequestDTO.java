package com.codeWithSrb.BookYourSlot.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class BookingInfoRequestDTO {

    @NotNull(message = "Date is required")
    @FutureOrPresent(message = "Date must not be in the past")
    private LocalDate date;

    @NotNull(message = "Start time is required")
    private LocalTime startTime;
}