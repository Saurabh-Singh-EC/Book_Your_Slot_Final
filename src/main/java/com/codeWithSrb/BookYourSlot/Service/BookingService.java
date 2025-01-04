package com.codeWithSrb.BookYourSlot.Service;

import com.codeWithSrb.BookYourSlot.Exception.ApiException;
import com.codeWithSrb.BookYourSlot.Model.BookingInfo;
import com.codeWithSrb.BookYourSlot.Model.UserInfo;
import com.codeWithSrb.BookYourSlot.Repository.BookingRepository;
import com.codeWithSrb.BookYourSlot.dto.BookingInfoRequestDTO;
import com.codeWithSrb.BookYourSlot.dto.CancelBookingInfoRequestDTO;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;

    public BookingService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    public List<LocalTime> findAllAvailableSlots(LocalDate localDate) {
        List<LocalTime> allSlots = generateSlots();

        Set<LocalTime> bookedSlots = findBookedSlots(localDate).stream()
                .map(BookingInfo::getStartTime)
                .collect(Collectors.toSet());

        return allSlots.stream()
                .filter(slot -> !bookedSlots.contains(slot))
                .toList();
    }

    private List<LocalTime> generateSlots() {
        return IntStream.rangeClosed(7, 21)
                .mapToObj(hour -> LocalTime.of(hour, 0))
                .toList();
    }

    private List<BookingInfo> findBookedSlots(LocalDate localDate) {
        return bookingRepository.findBookingInfoByDate(localDate);
    }

    public BookingInfo bookNewSlot(BookingInfoRequestDTO bookingInfoRequestDTO, UserInfo userInfo) {

        if(bookingRepository.findBookingInfoByDateAndStartTime(bookingInfoRequestDTO.getDate(), bookingInfoRequestDTO.getStartTime()) > 0)
            throw new ApiException("The selected time slot is already booked. Please choose a different time slot.");

        return bookingRepository.save(new BookingInfo(bookingInfoRequestDTO.getDate(), bookingInfoRequestDTO.getStartTime(), userInfo));
    }

    @Transactional
    public void cancelBooking(CancelBookingInfoRequestDTO cancelBookingInfoRequestDTO) {

        if (bookingRepository.findBookingInfoByIdDateAndStartTime(cancelBookingInfoRequestDTO.getBookingId(),
                cancelBookingInfoRequestDTO.getDate(),
                cancelBookingInfoRequestDTO.getStartTime()) <= 0)
            throw new ApiException("No booking found for the booking id: " + cancelBookingInfoRequestDTO.getBookingId());

        bookingRepository.deleteBooking(cancelBookingInfoRequestDTO.getBookingId());
    }
}