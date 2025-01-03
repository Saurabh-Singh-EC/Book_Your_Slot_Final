package com.codeWithSrb.BookYourSlot.Controller;


import com.codeWithSrb.BookYourSlot.Model.BookingInfo;
import com.codeWithSrb.BookYourSlot.Model.HttpResponse;
import com.codeWithSrb.BookYourSlot.Model.UserInfo;
import com.codeWithSrb.BookYourSlot.Service.BookingService;
import com.codeWithSrb.BookYourSlot.config.UserDetailsImpl;
import com.codeWithSrb.BookYourSlot.dto.BookingInfoRequestDTO;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import static com.codeWithSrb.BookYourSlot.dtomapper.BookingInfoDTOMapper.fromBookingInfo;
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
    public ResponseEntity<HttpResponse> bookSlot(@RequestBody @Valid BookingInfoRequestDTO bookingInfoRequestDTO, Authentication authentication) {

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
}