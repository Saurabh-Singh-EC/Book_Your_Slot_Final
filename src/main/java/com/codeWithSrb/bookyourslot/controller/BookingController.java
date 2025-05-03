package com.codeWithSrb.bookyourslot.controller;


import com.codeWithSrb.bookyourslot.model.BookingInfo;
import com.codeWithSrb.bookyourslot.model.HttpResponse;
import com.codeWithSrb.bookyourslot.model.UserInfo;
import com.codeWithSrb.bookyourslot.service.BookingService;
import com.codeWithSrb.bookyourslot.config.UserDetailsImpl;
import com.codeWithSrb.bookyourslot.dto.BookingInfoRequestDTO;
import com.codeWithSrb.bookyourslot.dto.CancelBookingInfoRequestDTO;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import static com.codeWithSrb.bookyourslot.dtomapper.BookingInfoDTOMapper.fromBookingInfo;
import static com.codeWithSrb.bookyourslot.dtomapper.BookingInfoDTOMapper.fromBookingInfoList;
import static java.time.LocalDateTime.now;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/v1/booking")
@Slf4j
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping("/slots/{date}")
    public ResponseEntity<HttpResponse> availableSlots(@PathVariable LocalDate date) {

        List<LocalTime> availableSlots = bookingService.findAllAvailableSlots(date);

        return ResponseEntity.ok()
                .body(HttpResponse.builder()
                        .timeStamp(now().toString())
                        .httpStatus(OK)
                        .statusCode(OK.value())
                        .message("Available slots retrieved")
                        .data(Map.of("AvailableSlots", availableSlots))
                        .build());
    }

    @PostMapping("/book")
    public ResponseEntity<HttpResponse> createBooking(@RequestBody @Valid BookingInfoRequestDTO bookingInfoRequestDTO, Authentication authentication) {

        UserDetailsImpl userDetailsImpl = (UserDetailsImpl) authentication.getPrincipal();
        UserInfo userInfo = userDetailsImpl.getUserInfo();

        BookingInfo bookingInfo = bookingService.bookNewSlot(bookingInfoRequestDTO, userInfo);

        return ResponseEntity.ok()
                .body(HttpResponse.builder()
                        .timeStamp(now().toString())
                        .httpStatus(CREATED)
                        .statusCode(CREATED.value())
                        .message("Booking Confirmed")
                        .data(Map.of("BookingInformation", fromBookingInfo(bookingInfo, userInfo)))
                        .build());
    }

    @GetMapping
    public ResponseEntity<HttpResponse> userBookings(Authentication authentication) {

        UserDetailsImpl userDetailsImpl = (UserDetailsImpl) authentication.getPrincipal();
        UserInfo userInfo = userDetailsImpl.getUserInfo();

        List<BookingInfo> usersBooking = bookingService.getUsersBooking(userInfo);

        return ResponseEntity.ok()
                .body(HttpResponse.builder()
                        .timeStamp(now().toString())
                        .httpStatus(OK)
                        .statusCode(OK.value())
                        .data(Map.of("bookings", fromBookingInfoList(usersBooking)))
                        .message("Bookings Retrieved")
                        .build());
    }

    @PostMapping("/cancel")
    public ResponseEntity<HttpResponse> cancelBooking(@RequestBody @Valid CancelBookingInfoRequestDTO cancelBookingInfoRequestDTO) {

        bookingService.cancelBooking(cancelBookingInfoRequestDTO);

        return ResponseEntity.ok()
                .body(HttpResponse.builder()
                        .timeStamp(now().toString())
                        .httpStatus(OK)
                        .statusCode(OK.value())
                        .message("Booking canceled")
                        .build());
    }
}