package com.example.bookings.Repo;

import com.example.bookings.DTO.VacantHotelsDTO;
import com.example.bookings.Models.Bookings;
import jakarta.persistence.Tuple;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepo extends JpaRepository<Bookings, Long> {
    @Query("SELECT b from Bookings b where b.orderHotelId=:hotelId and b.orderStatus='ACTIVE' order by b.id desc")
    public List<Bookings> getBookingsByHotelId(Long hotelId);

    @Query("select b.orderHotelId, b.orderRoomCategoryId, count(b.id) from Bookings b where b.orderStatus='ACTIVE' and b.orderHotelId in (?1) " +
            "and ((b.orderStartTime<=?2 and b.orderEndTime>=?2) or (b.orderEndTime>=?3 and b.orderStartTime<=?3)) group by b.orderHotelId, b.orderRoomCategoryId")
    public List<Tuple> getBookedHotels(List<Long> hotelIds, LocalDate startDate, LocalDate endDate);

    @Query("select count(b.id) from Bookings b where (b.orderStatus<>'FAILED' and (b.orderStatus in ('RESERVED', 'CREATED') " +
            "and (?1 - b.orderPaymentExpiredTs) <= 60000)) OR b.orderStatus = 'ACTIVE' AND b.orderRoomCategoryId=?2")
    Optional<Long> countOfActiveOrder(Long current_ts, Long roomCategoryId);

    @Query("SELECT b from Bookings b where b.orderRefId=?1 and b.orderStatus NOT IN ('ARCHIVED') order by b.id desc")
    Optional<Bookings> findBookingByOrderRefId(String bookingReferenceID);

    @Query("SELECT b from Bookings b where b.orderRefId in (?1) AND b.orderStatus NOT IN ('FAILED', 'ARCHIVED')")
    List<Bookings> findBookingsByRefIds(List<String> bookingRedIds);


    @Modifying(clearAutomatically=true)
    @Transactional
    @Query(value = "UPDATE Bookings b SET b.orderStatus = 'ARCHIVED' where b.orderRefId in (?1) ")
    void deleteBookingsByRefId(List<String> activeBookings);

    @Query("SELECT b.orderRefId from Bookings b where b.orderEndTime = ?1 AND b.orderStatus = 'ACTIVE'")
    List<String> getReferenceByIdByDate(LocalDate currDate);
}
