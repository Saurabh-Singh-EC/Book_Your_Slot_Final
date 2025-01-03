package com.codeWithSrb.BookYourSlot.Repository;

import com.codeWithSrb.BookYourSlot.Model.BookingInfo;
import org.springframework.data.jpa.repository.JpaRepository;
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
            value = "select count(*) from BookYourSlot.booking_info bi where bi.date = :localDate and bi.start_time= :localTime")
    int findBookingInfoByDateAndStartTime(LocalDate localDate, LocalTime localTime);
}
