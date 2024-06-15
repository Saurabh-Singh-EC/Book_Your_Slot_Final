package com.codeWithSrb.BookYourSlot.Service;

import com.codeWithSrb.BookYourSlot.Model.BookingForm;
import com.codeWithSrb.BookYourSlot.Model.BookingInfo;
import com.codeWithSrb.BookYourSlot.Model.UserInfo;
import com.codeWithSrb.BookYourSlot.Repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;

    @Autowired
    public BookingService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    public void createNewBooking(BookingForm bookingForm, UserInfo userInfo) {
        bookingRepository.save(new BookingInfo(bookingForm.getEmail(), bookingForm.getDate(), bookingForm.getStartTime(), bookingForm.getEndTime(), userInfo));
    }
}