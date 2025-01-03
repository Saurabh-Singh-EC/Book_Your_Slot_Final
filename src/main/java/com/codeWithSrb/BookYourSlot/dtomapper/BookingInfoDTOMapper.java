package com.codeWithSrb.BookYourSlot.dtomapper;

import com.codeWithSrb.BookYourSlot.Model.BookingInfo;
import com.codeWithSrb.BookYourSlot.Model.UserInfo;
import com.codeWithSrb.BookYourSlot.dto.BookingInfoResponseDTO;

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
}