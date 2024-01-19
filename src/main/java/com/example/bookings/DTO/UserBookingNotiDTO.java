package com.example.bookings.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class UserBookingNotiDTO {
    String hotelName;
    String roomName;
    String hotelAddress;
    Long userId;
    LocalDate orderDate;
    LocalDate orderStartDate;
    LocalDate orderEndDate;
    String orderRefId;
    float orderAmount;
    List<String> allUsers;
    String status;
}
