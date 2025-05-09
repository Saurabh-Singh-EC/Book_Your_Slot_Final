package com.codeWithSrb.bookyourslot.service;

import com.codeWithSrb.bookyourslot.exception.ApiException;
import com.codeWithSrb.bookyourslot.model.BookingInfo;
import com.codeWithSrb.bookyourslot.model.UserInfo;
import com.codeWithSrb.bookyourslot.repository.BookingRepository;
import com.codeWithSrb.bookyourslot.dto.BookingInfoRequestDTO;
import com.codeWithSrb.bookyourslot.dto.CancelBookingInfoRequestDTO;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
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

    public List<BookingInfo> findBookedSlots(LocalDate localDate) {
        return bookingRepository.findBookingInfoByDate(localDate);
    }

    @Transactional
    public BookingInfo bookNewSlot(BookingInfoRequestDTO bookingInfoRequestDTO, UserInfo userInfo) {

        try {
            return bookingRepository.save(new BookingInfo(bookingInfoRequestDTO.getDate(), bookingInfoRequestDTO.getStartTime(), userInfo));
        } catch (DataIntegrityViolationException exception) {
            throw new ApiException("The selected time slot is already booked. Please choose a different time slot.");
        }
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void cancelBooking(CancelBookingInfoRequestDTO cancelBookingInfoRequestDTO) {

        BookingInfo bookingInfo = bookingRepository
                .findBookingInfoByIdDateAndStartTime(
                        cancelBookingInfoRequestDTO.getBookingId(),
                        cancelBookingInfoRequestDTO.getDate(),
                        cancelBookingInfoRequestDTO.getStartTime()
                )
                .orElseThrow(() -> new ApiException("The booking was already canceled or does not exist."));

        bookingRepository.delete(bookingInfo);
    }

    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public List<BookingInfo> getUsersBooking(UserInfo userInfo) {
        return bookingRepository.getBookingInfoById(userInfo.getId());
    }
}