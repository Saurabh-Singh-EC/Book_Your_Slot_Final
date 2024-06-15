package com.codeWithSrb.BookYourSlot.Model;


import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

import java.util.Date;

@Entity
@Table(name = "booking_info")
public class BookingInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotEmpty(message = "EMail id cannot be empty")
    @Email(message = "Invalid email. Please enter a valid email address")
    private String email;

    private Date date;

    private String startTime;
    private String endTime;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "fkUserId")
    private UserInfo userInfo;

    public BookingInfo() {
    }

    public BookingInfo(String email, Date date, String startTime, String endTime, UserInfo userInfo) {
        this.email = email;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.userInfo = userInfo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }
}
