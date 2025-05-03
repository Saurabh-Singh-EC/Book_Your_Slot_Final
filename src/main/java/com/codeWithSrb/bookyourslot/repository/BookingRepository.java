package com.codeWithSrb.bookyourslot.repository;

import com.codeWithSrb.bookyourslot.model.BookingInfo;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<BookingInfo, Integer> {

    @Lock(LockModeType.PESSIMISTIC_READ)
    @Query("select bi from BookingInfo bi where bi.date= :localDate")
    List<BookingInfo> findBookingInfoByDate(
            @Param("localDate") LocalDate localDate
    );


    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT bi FROM BookingInfo bi WHERE bi.id = :bookingId AND bi.date = :localDate AND bi.startTime = :startTime")
    Optional<BookingInfo> findBookingInfoByIdDateAndStartTime(
            @Param("bookingId") int bookingId,
            @Param("localDate") LocalDate localDate,
            @Param("startTime") LocalTime startTime
    );

    @Query("select bi from BookingInfo bi where bi.userInfo.id= :id")
    List<BookingInfo> getBookingInfoById(@Param("id") int id);
}
