package com.codeWithSrb.BookYourSlot.Model;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "booking_info")
public class BookingInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private LocalDate date;

    @Column(name = "start_time")
    private LocalTime startTime;

    @ManyToOne
    @JoinColumn(name = "fk_user_id")
    private UserInfo userInfo;

    public BookingInfo(LocalDate date, LocalTime startTime, UserInfo userInfo) {
        this.date = date;
        this.startTime = startTime;
        this.userInfo = userInfo;
    }
}
