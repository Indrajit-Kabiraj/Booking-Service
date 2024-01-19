package com.example.bookings.DTO;

import com.example.bookings.DTO.UserDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingOrderDTO {
    private Long orderHotelId;

    private Long orderRoomCategoryId;

    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate orderDate;

    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate orderStartTime;

    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate orderEndTime;

    private String orderStatus;

    private Long orderDuration;

    private List<UserDTO> users;

    private Long parentUserId;

    private String bookingRefId;

    private Long maxRoomNumber;

    private float roomPrice;

    public BookingOrderDTO(String bookingMessage) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        BookingOrderDTO bookingOrderDTO = mapper.readValue(bookingMessage, BookingOrderDTO.class);
        this.orderHotelId = bookingOrderDTO.getOrderHotelId();
        this.orderRoomCategoryId = bookingOrderDTO.getOrderRoomCategoryId();
        this.orderDate = bookingOrderDTO.getOrderDate();
        this.orderEndTime = bookingOrderDTO.getOrderEndTime();
        this.orderStartTime = bookingOrderDTO.getOrderStartTime();
        this.orderStatus = bookingOrderDTO.getOrderStatus();
        this.orderDuration = bookingOrderDTO.getOrderDuration();
        this.bookingRefId = bookingOrderDTO.getBookingRefId();
        this.maxRoomNumber = bookingOrderDTO.getMaxRoomNumber();
        this.parentUserId = bookingOrderDTO.getParentUserId();
        this.users = bookingOrderDTO.getUsers();
        this.roomPrice = bookingOrderDTO.getRoomPrice();
    }

    @Override
    public String toString() {
        return "BookingOrderDTO{" +
                "orderHotelId=" + orderHotelId +
                ", orderRoomCategoryId=" + orderRoomCategoryId +
                ", orderDate=" + orderDate +
                ", orderStartTime=" + orderStartTime +
                ", orderEndTime=" + orderEndTime +
                ", orderStatus='" + orderStatus + '\'' +
                ", orderDuration=" + orderDuration +
                ", users=" + users +
                ", parentUserId=" + parentUserId +
                ", bookingRefId='" + bookingRefId + '\'' +
                ", maxRoomNumber=" + maxRoomNumber +
                '}';
    }
}
