package com.example.bookings.Models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

@Entity
@RequiredArgsConstructor
public class BookingUsers {
    @Id
    @SequenceGenerator(name="idSequenceBookingUsers", sequenceName="ID_SEQUENCE_BOOKINGUSERS", allocationSize=1, initialValue = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idSequenceBookingUsers")
    Long id;

    String email;

    String name;

    @Column(name = "booking_ref_id")
    String bookingRefId;

    public BookingUsers(String email, String fullName, String bookingReferenceId) {
        this.email = email;
        this.name = fullName;
        this.bookingRefId = bookingReferenceId;
    }
}
