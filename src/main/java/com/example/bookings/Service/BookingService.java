package com.example.bookings.Service;

import com.example.bookings.Configs.RabbitMqConfig;
import com.example.bookings.DTO.*;
import com.example.bookings.DTO.BookingOrderDTO;

import com.example.bookings.Exceptions.BookingNotPossibleException;
import com.example.bookings.Models.BookingUsers;
import com.example.bookings.Models.Bookings;
import com.example.bookings.Repo.BookingRepo;

import com.example.bookings.Repo.BookingUserRepo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.persistence.Tuple;
import lombok.AllArgsConstructor;

import okhttp3.*;
import org.json.JSONObject;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.io.IOException;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class BookingService {

    @Autowired
    BookingRepo bookingRepo;

    @Autowired
    BookingUserService bookingUserService;

    @Autowired
    RabbitTemplate rabbitTemplate;

    public CompletionStage<Map<String, Object>> createBooking(BookingOrderDTO newBooking) throws BookingNotPossibleException {
        System.out.println(newBooking.toString());
        Long current_ts = System.currentTimeMillis();
        Optional<Long> isCreationPossible = bookingRepo.countOfActiveOrder(current_ts, newBooking.getOrderRoomCategoryId());
        if(isCreationPossible.isPresent() && isCreationPossible.get() < newBooking.getMaxRoomNumber()){
            Map<String, Object> res = new HashMap<>();
            Bookings booking = new Bookings();
            booking.setOrderHotelId(newBooking.getOrderHotelId());
            booking.setOrderRoomCategoryId(newBooking.getOrderRoomCategoryId());
            booking.setOrderedByUserId(newBooking.getParentUserId());
            booking.setOrderDate(newBooking.getOrderDate());
            booking.setOrderStartTime(newBooking.getOrderStartTime());
            booking.setOrderEndTime(newBooking.getOrderEndTime());
            booking.setOrderStatus("CREATED");
            booking.setOrderDuration(newBooking.getOrderDuration());
            booking.setOrderPaymentExpiredTs(current_ts);
            booking.setOrderRefId(newBooking.getBookingRefId());
            booking.setBookingAmount(newBooking.getRoomPrice());
            System.out.println(booking.toString());
            bookingRepo.save(booking);
            bookingUserService.createUserEntryForBooking(newBooking.getBookingRefId(), newBooking.getUsers());
            res.put("status","SUCCESS");
            res.put("data", booking);
            return CompletableFuture.completedFuture(res);
        }
        else{
            throw new BookingNotPossibleException("Booking has failed, please try again!");
        }
    }

    public CompletionStage<List<Bookings>> getBookingsForHotel(Long hotelId) {
        return CompletableFuture.completedFuture(bookingRepo.getBookingsByHotelId(hotelId));
    }

    public CompletionStage<Bookings> updateBooking(Long id, Bookings booking) {
        Optional<Bookings> existingBooking = bookingRepo.findById(id);
        if(existingBooking.isPresent()){
            existingBooking = updateBookingDetails(existingBooking,booking);
        }
        return CompletableFuture.completedFuture(existingBooking.get());
    }

    private Optional<Bookings> updateBookingDetails(Optional<Bookings> existingBooking, Bookings booking) {
        if(booking.getOrderDate() != null) existingBooking.get().setOrderDate(booking.getOrderDate());
        if(booking.getOrderHotelId() != null) existingBooking.get().setOrderHotelId(booking.getOrderHotelId());
        if(booking.getOrderRoomCategoryId() != null) existingBooking.get().setOrderRoomCategoryId(booking.getOrderRoomCategoryId());
        if(booking.getOrderDuration() != null) existingBooking.get().setOrderDuration(booking.getOrderDuration());
        if(booking.getOrderStatus() != null) existingBooking.get().setOrderStatus(booking.getOrderStatus());
        if(booking.getOrderStartTime() != null) existingBooking.get().setOrderStartTime(booking.getOrderStartTime());
        if(booking.getOrderEndTime() != null) existingBooking.get().setOrderEndTime(booking.getOrderEndTime());
        if(booking.getOrderedByUserId() != null) existingBooking.get().setOrderedByUserId(booking.getOrderedByUserId());
        return existingBooking;
    }

    public CompletionStage<Object> deleteBooking(Long bookingId) {
        Optional<Bookings> booking = bookingRepo.findById(bookingId);
        if(booking.isPresent() == true){
            booking.get().setOrderStatus("ARCHIVED");
            bookingRepo.save(booking.get());
        }
        Map<String, String> res = new HashMap<>();
        res.put("Delete_status","SUCCESS");
        res.put("Deleted_Booking_ID", (bookingId).toString());
        return CompletableFuture.completedFuture(res);
    }

    public CompletionStage<Map<String, List<VacantHotelsDTO>>> getBookedRooms(SearchDTO hotelSearchDTO) throws IOException {

        List<Tuple> bookedHotels = bookingRepo.getBookedHotels(hotelSearchDTO.getHotelId(),hotelSearchDTO.getStartTime(),hotelSearchDTO.getEndTime());
        List<VacantHotelsDTO> hotelBookings = bookedHotels.stream()
                .map(t -> new VacantHotelsDTO(
                        t.get(0, Long.class),
                        t.get(1, Long.class),
                        t.get(2, Long.class)
                ))
                .collect(Collectors.toList());
        Map<String, List<VacantHotelsDTO>> res = new HashMap<>();
        res.put("bookedRoons", hotelBookings);
        return CompletableFuture.completedFuture(res);
    }

    public CompletionStage<Map<String, String>> updateBookingStatus(String bookingReferenceID, String status) throws BookingNotPossibleException {
        Optional<Bookings> booking = bookingRepo.findBookingByOrderRefId(bookingReferenceID);
        Map<String, String> res = new HashMap<>();
        if(booking.isPresent()){
            booking.get().setOrderStatus(status);
            bookingRepo.save(booking.get());
            res.put("booking_ref_id", bookingReferenceID);
            res.put("update_status", "SUCCESSFUL");
        }
        else{
            throw new BookingNotPossibleException("Booking has failed, please try again!");
        }
        return CompletableFuture.completedFuture(res);
    }

    public CompletionStage<Map<String, Object>> deleteBookingBulk(List<String> bookingRedIds) throws BookingNotPossibleException {
        List<Bookings> activeBookings = bookingRepo.findBookingsByRefIds(bookingRedIds);
        List<String> activeRefIds = activeBookings.stream().map(b -> b.getOrderRefId()).collect(Collectors.toList());
        if(activeRefIds.size() == 0){
            throw new BookingNotPossibleException("No active bookings found!");
        }
        bookingRepo.deleteBookingsByRefId(activeRefIds);
        Map<String, Object> res = new HashMap<>();
        res.put("status", "SUCCESS");
        res.put("archived_bookings", activeRefIds);
        return CompletableFuture.completedFuture(res);
    }

    @Transactional
    public CompletionStage<Map<String, Object>> deleteBookingInBulkScheduled() throws BookingNotPossibleException {
        LocalDate currDate = LocalDate.now();
        System.out.println(currDate);
        List<String> bookingsToBeDeleted = bookingRepo.getReferenceByIdByDate(currDate);
        if(bookingsToBeDeleted.size() == 0){
            throw new BookingNotPossibleException("No active bookings found!");
        }
        bookingRepo.deleteBookingsByRefId(bookingsToBeDeleted);
        Map<String, Object> res = new HashMap<>();
        res.put("status", "SUCCESS");
        res.put("archived_bookings", bookingsToBeDeleted);
        return CompletableFuture.completedFuture(res);
    }

    public void sendUpdateToUserUponSuccess(String bookingRefId) {
        System.out.println("upon success" + bookingRefId);
        Optional<Bookings> booking = bookingRepo.findBookingByOrderRefId(bookingRefId);
        Long parentUserId = booking.get().getOrderedByUserId();
        List<String> allUsers = bookingUserService.getAllUsersByRefId(bookingRefId);
        String hotelName = null;
        String roomName = null;
        String hotelAddress = null;
        LocalDate bookingDate = booking.get().getOrderDate();
        LocalDate bookingStartDate = booking.get().getOrderStartTime();
        LocalDate bookingEndDate = booking.get().getOrderEndTime();
        OkHttpClient client = new OkHttpClient();
        String URL = "http://localhost:8080/v1/rooms/getInfo?hotelId="+booking.get().getOrderHotelId()+"&roomCategoryId="+booking.get().getOrderRoomCategoryId();
        Request request = new Request.Builder().url(URL).header("Content-Type","application/json").build();
        try (Response response = client.newCall(request).execute()) {
            String resp = response.body().string();
            JSONObject resData = new JSONObject(resp);
            JSONObject data = resData.getJSONObject("data");
            hotelName  = (String) data.get("hotelName");
            roomName = (String) data.get("roomName");
            hotelAddress = (String) data.get("hotelAddress");
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            UserBookingNotiDTO userBookingNotiDTO = new UserBookingNotiDTO(hotelName, roomName, hotelAddress, parentUserId, bookingDate,
                    bookingStartDate, bookingEndDate, bookingRefId, booking.get().getBookingAmount(), allUsers, "SUCCESS");
            String queuePayloadString = objectMapper.writeValueAsString(userBookingNotiDTO);
            rabbitTemplate.convertAndSend("","q.user-notification_q",queuePayloadString);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
