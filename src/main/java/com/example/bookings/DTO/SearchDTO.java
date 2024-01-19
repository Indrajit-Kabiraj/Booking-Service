package com.example.bookings.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.NonNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
public class SearchDTO {
    private List<Long> hotelId;

    private LocalDate startTime;

    private LocalDate endTime;

    public List<Long> getHotelId() {
        return hotelId;
    }

    public void setHotelId(List<Long> hotelId) {
        this.hotelId = hotelId;
    }

    public LocalDate getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDate startTime) {
        this.startTime = startTime;
    }

    public LocalDate getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDate endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "SearchDTO{" +
                "hotelId=" + hotelId +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }
}
