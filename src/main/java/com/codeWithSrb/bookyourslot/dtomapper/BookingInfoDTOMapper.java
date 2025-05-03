package com.codeWithSrb.bookyourslot.dtomapper;

import com.codeWithSrb.bookyourslot.model.BookingInfo;
import com.codeWithSrb.bookyourslot.model.UserInfo;
import com.codeWithSrb.bookyourslot.dto.BookingInfoResponseDTO;

import java.util.List;

public class BookingInfoDTOMapper {

    public static BookingInfoResponseDTO fromBookingInfo(BookingInfo bookingInfo, UserInfo userInfo) {

        return new BookingInfoResponseDTO(
                bookingInfo.getId(),
                bookingInfo.getDate(),
                bookingInfo.getStartTime(),
                userInfo.getFirstName(),
                userInfo.getLastName()
        );
    }

    public static List<BookingInfoResponseDTO> fromBookingInfoList(List<BookingInfo> usersBooking) {

        return usersBooking.stream()
                .map(bookingInfo -> new BookingInfoResponseDTO(
                        bookingInfo.getId(),
                        bookingInfo.getDate(),
                        bookingInfo.getStartTime()
                )).toList();
    }
}