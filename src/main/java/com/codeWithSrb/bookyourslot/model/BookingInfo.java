package com.codeWithSrb.bookyourslot.model;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@Entity
@Table(
        name = "booking_info",
        uniqueConstraints = @UniqueConstraint(columnNames = {"date", "start_time"})
)
public class BookingInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @ManyToOne
    @JoinColumn(name = "fk_user_id", nullable = false)
    private UserInfo userInfo;

    public BookingInfo(LocalDate date, LocalTime startTime, UserInfo userInfo) {
        this.date = date;
        this.startTime = startTime;
        this.userInfo = userInfo;
    }
}
