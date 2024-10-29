package com.codeWithSrb.BookYourSlot.Model;


import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@Entity
@Table(name = "booking_info")
public class BookingInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotEmpty(message = "Email id cannot be empty")
    @Email(message = "Invalid email. Please enter a valid email address")
    private String email;

    private Date date;

    @Column(name = "start_time")
    private String startTime;

    @Column(name = "end_time")
    private String endTime;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "fk_user_id")
    private UserInfo userInfo;

    public BookingInfo(String email, Date date, String startTime, String endTime, UserInfo userInfo) {
        this.email = email;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.userInfo = userInfo;
    }
}
