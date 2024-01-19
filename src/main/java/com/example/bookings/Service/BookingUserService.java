package com.example.bookings.Service;

import com.example.bookings.DTO.UserDTO;
import com.example.bookings.Models.BookingUsers;
import com.example.bookings.Repo.BookingUserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BookingUserService {
    
    @Autowired
    private final BookingUserRepo bookingUserRepo;
    
    public Map<String,Object> createUserEntryForBooking(String bookingReferenceId, List<UserDTO> users){
        users.forEach(user->{
            String fullName = user.getFirstName() + " " + user.getLastName();
            BookingUsers currUser = new BookingUsers(user.getEmail(), fullName, bookingReferenceId); 
            bookingUserRepo.save(currUser);
        });
        Map<String, Object> res = new HashMap<>();
        res.put("booking_ref_id", bookingReferenceId);
        res.put("users_added", users);
        return res;
    }

    public List<String> getAllUsersByRefId(String bookingRefId) {
        List<String> bookingUsers = bookingUserRepo.getUsersByRefId(bookingRefId);
        return bookingUsers;
    }
}
