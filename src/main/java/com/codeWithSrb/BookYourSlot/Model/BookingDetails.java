package com.codeWithSrb.BookYourSlot.Model;

import jakarta.persistence.*;

import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "booking_details")
public class BookingDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "First_Name")
    private String userFirstName;

    @Column(name = "Last_Name")
    private String userLastName;

    @Column(name = "Booking_Time")
    private Timestamp bookingTime;

    public BookingDetails() {
    }

    public BookingDetails(int id, String userFirstName, String userLastName, Timestamp bookingTime) {
        this.id = id;
        this.userFirstName = userFirstName;
        this.userLastName = userLastName;
        this.bookingTime = bookingTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserFirstName() {
        return userFirstName;
    }

    public void setUserFirstName(String userFirstName) {
        this.userFirstName = userFirstName;
    }

    public String getUserLastName() {
        return userLastName;
    }

    public void setUserLastName(String userLastName) {
        this.userLastName = userLastName;
    }

    public Timestamp getBookingTime() {
        return bookingTime;
    }

    public void setBookingTime(Timestamp bookingTime) {
        this.bookingTime = bookingTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BookingDetails)) return false;
        BookingDetails that = (BookingDetails) o;
        return id == that.id && userFirstName.equals(that.userFirstName) && userLastName.equals(that.userLastName) && bookingTime.equals(that.bookingTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userFirstName, userLastName, bookingTime);
    }

    @Override
    public String toString() {
        return "BookingDetails{" +
                "id=" + id +
                ", userFirstName='" + userFirstName + '\'' +
                ", userLastName='" + userLastName + '\'' +
                ", bookingTime=" + bookingTime +
                '}';
    }
}
