package com.codeWithSrb.bookyourslot.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookingInfoResponseDTO {
    private int bookingId;
    private LocalDate date;
    private LocalTime startTime;
    private String firstName;
    private String lastName;

    public BookingInfoResponseDTO(int bookingId, LocalDate date, LocalTime startTime) {
        this.bookingId = bookingId;
        this.date = date;
        this.startTime = startTime;
    }
}