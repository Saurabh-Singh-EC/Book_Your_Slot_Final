package com.codeWithSrb.BookYourSlot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingInfoResponseDTO {
    private int bookingId;
    private LocalDate date;
    private LocalTime startTime;
    private String firstName;
    private String lastName;
}