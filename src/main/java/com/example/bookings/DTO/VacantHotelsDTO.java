package com.example.bookings.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
@AllArgsConstructor
public class VacantHotelsDTO{

    Long hotelId;
    Long roomCategoryId;
    Long numberOfVacantRooms;

    public Long getHotelId() {
        return hotelId;
    }

    public void setHotelId(Long hotelId) {
        this.hotelId = hotelId;
    }

    public Long getRoomCategoryId() {
        return roomCategoryId;
    }

    public void setRoomCategoryId(Long roomCategoryId) {
        this.roomCategoryId = roomCategoryId;
    }

    public Long getNumberOfVacantRooms() {
        return numberOfVacantRooms;
    }

    public void setNumberOfVacantRooms(Long numberOfVacantRooms) {
        this.numberOfVacantRooms = numberOfVacantRooms;
    }

    @Override
    public String toString() {
        return "VacantHotelsDTO{" +
                "hotelId=" + hotelId +
                ", roomCategoryId=" + roomCategoryId +
                ", numberOfVacantRooms=" + numberOfVacantRooms +
                '}';
    }
}
