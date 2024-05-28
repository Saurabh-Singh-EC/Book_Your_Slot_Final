package com.codeWithSrb.BookYourSlot.Service;

import com.codeWithSrb.BookYourSlot.Model.BookingDetails;
import com.codeWithSrb.BookYourSlot.Repository.BookingRepository;
import com.codeWithSrb.BookYourSlot.Exception.InvalidRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    public List<BookingDetails> retrieveAllBooking(){
        List<BookingDetails> bookings = bookingRepository.findAll();
        bookings.forEach(booking -> System.out.println(booking.getUserFirstName()));
        return bookings;
    }

    public BookingDetails retrieveBooking(int id) {
        Optional<BookingDetails> booking = bookingRepository.findById(id);

        if(booking.isPresent()) {
            return booking.get();
        } else {
            throw new InvalidRequestException("No booking found for id: " + id);
        }
    }

    public BookingDetails createBooking(BookingDetails bookingDetails) {
        BookingDetails response = bookingRepository.save(bookingDetails);
        System.err.printf("successfully created a new booking with Id %s", response.getId());
        return response;
    }

    public String deleteBooking(int id) {
        Optional<BookingDetails> bookingDetails = bookingRepository.findById(id);
        if (bookingDetails.isPresent()) {
            bookingRepository.deleteById(id);
            return "Booking has been delete for id " + id;
        }
        return "No booking exists for id " + id;
    }

    public String updateExistingBooking(BookingDetails bookingDetails, int id) {
        Optional<BookingDetails> existingBookingDetails = bookingRepository.findById(id);
        if (existingBookingDetails.isPresent()) {
            existingBookingDetails.get().setUserFirstName(bookingDetails.getUserFirstName());
            existingBookingDetails.get().setUserLastName(bookingDetails.getUserLastName());
            existingBookingDetails.get().setBookingTime(bookingDetails.getBookingTime());
            bookingRepository.save(existingBookingDetails.get());
            return "Booking updated for id: " + id;
        } else {
            return "Booking with user id: " + id + " is not present";
        }
    }

    public boolean checkIfAlreadyBookingExists(BookingDetails bookingDetails) {
        Optional<BookingDetails> details = bookingRepository.findByUserFirstName(bookingDetails.getUserFirstName());
        return details.isPresent();
    }
}