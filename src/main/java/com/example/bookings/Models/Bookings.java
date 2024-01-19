package com.example.bookings.Models;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NonNull;

import java.time.LocalDate;

@Entity
@AllArgsConstructor
public class Bookings {
    @Id
    @SequenceGenerator(name="idSequenceBookings", sequenceName="ID_SEQUENCE_BOOKINGS", allocationSize=1, initialValue = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idSequenceBookings")
    private Long id;

    @Column(name = "order_hotel_id")
    @NonNull
    private Long orderHotelId;

    @Column(name = "order_room_category_id")
    @NonNull
    private Long orderRoomCategoryId;

    @Column(name = "ordered_by_user_id")
    private Long orderedByUserId;

    @Column(name = "order_date")
    @NonNull
    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate orderDate;

    @Column(name = "order_start_time")
    @NonNull
    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate orderStartTime;

    @Column(name = "order_end_time")
    @NonNull
    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate orderEndTime;

    @Column(name = "order_status")
    private String orderStatus;

    @Column(name = "order_duration")
    private Long orderDuration;

    @Column(name = "order_ref_id")
    private String orderRefId;

    @Column(name = "order_payment_expired_ts")
    private Long orderPaymentExpiredTs;

    @Column(name = "booking_amount")
    private float bookingAmount;

    public Bookings(){

    }

    public Bookings(@NonNull Long orderHotelId, @NonNull Long orderRoomCategoryId, Long orderedByUserId, @NonNull LocalDate orderDate, @NonNull LocalDate orderStartTime, @NonNull LocalDate orderEndTime, String orderStatus, Long orderDuration, String orderRefId, Long orderPaymentExpiredTs, float bookingAmount) {
        this.orderHotelId = orderHotelId;
        this.orderRoomCategoryId = orderRoomCategoryId;
        this.orderedByUserId = orderedByUserId;
        this.orderDate = orderDate;
        this.orderStartTime = orderStartTime;
        this.orderEndTime = orderEndTime;
        this.orderStatus = orderStatus;
        this.orderDuration = orderDuration;
        this.orderRefId = orderRefId;
        this.orderPaymentExpiredTs = orderPaymentExpiredTs;
        this.bookingAmount = bookingAmount;
    }

    public float getBookingAmount() {
        return bookingAmount;
    }

    public void setBookingAmount(float bookingAmount) {
        this.bookingAmount = bookingAmount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrderHotelId() {
        return orderHotelId;
    }

    public void setOrderHotelId(Long orderHotelId) {
        this.orderHotelId = orderHotelId;
    }

    public Long getOrderedByUserId() {
        return orderedByUserId;
    }

    public void setOrderedByUserId(Long orderedByUserId) {
        this.orderedByUserId = orderedByUserId;
    }

    public Long getOrderRoomCategoryId() {
        return orderRoomCategoryId;
    }

    public void setOrderRoomCategoryId(Long orderRoomCategoryId) {
        this.orderRoomCategoryId = orderRoomCategoryId;
    }

    public LocalDate getOrderStartTime() {
        return orderStartTime;
    }

    public void setOrderStartTime(LocalDate orderStartTime) {
        this.orderStartTime = orderStartTime;
    }

    public LocalDate getOrderEndTime() {
        return orderEndTime;
    }

    public void setOrderEndTime(LocalDate orderEndTime) {
        this.orderEndTime = orderEndTime;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Long getOrderDuration() {
        return orderDuration;
    }

    public void setOrderDuration(Long orderDuration) {
        this.orderDuration = orderDuration;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }

    public String getOrderRefId() {
        return orderRefId;
    }

    public void setOrderRefId(String orderRefId) {
        this.orderRefId = orderRefId;
    }

    public Long getOrderPaymentExpiredTs() {
        return orderPaymentExpiredTs;
    }

    public void setOrderPaymentExpiredTs(Long orderPaymentExpiredTs) {
        this.orderPaymentExpiredTs = orderPaymentExpiredTs;
    }

    @Override
    public String toString() {
        return "Bookings{" +
                "id=" + id +
                ", orderHotelId=" + orderHotelId +
                ", orderRoomCategoryId=" + orderRoomCategoryId +
                ", orderedByUserId=" + orderedByUserId +
                ", orderDate=" + orderDate +
                ", orderStartTime=" + orderStartTime +
                ", orderEndTime=" + orderEndTime +
                ", orderStatus='" + orderStatus + '\'' +
                ", orderDuration=" + orderDuration +
                ", orderRefId='" + orderRefId + '\'' +
                ", orderPaymentExpiredTs=" + orderPaymentExpiredTs +
                '}';
    }
}
