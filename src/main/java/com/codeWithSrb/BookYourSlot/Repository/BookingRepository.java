package com.codeWithSrb.BookYourSlot.Repository;

import com.codeWithSrb.BookYourSlot.Model.BookingInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<BookingInfo, Integer> {

    @Query(nativeQuery = true,
            value = "select * from BookYourSlot.booking_info bi where bi.date= :localDate")
    List<BookingInfo> findBookingInfoByDate(LocalDate localDate);

    @Query(nativeQuery = true,
            value = "select count(*) from BookYourSlot.booking_info bi where bi.date = :localDate and bi.start_time= :startTime")
    int findBookingInfoByDateAndStartTime(LocalDate localDate, LocalTime startTime);


    @Query(nativeQuery = true,
            value = "select count(*) from BookYourSlot.booking_info bi where bi.id = :bookingId and bi.date = :localDate and bi.start_time= :startTime")
    int findBookingInfoByIdDateAndStartTime(int bookingId, LocalDate localDate, LocalTime startTime);

    @Modifying
    @Query(nativeQuery = true, value = "delete from BookYourSlot.booking_info bi where bi.id = :bookingId")
    void deleteBooking(int bookingId);
}
