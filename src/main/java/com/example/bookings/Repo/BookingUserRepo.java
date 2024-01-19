package com.example.bookings.Repo;

import com.example.bookings.Models.BookingUsers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingUserRepo extends JpaRepository<BookingUsers, Long> {

    @Query("SELECT b.name from BookingUsers b where b.bookingRefId=?1")
    List<String> getUsersByRefId(String bookingRefId);
}
