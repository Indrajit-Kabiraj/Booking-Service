package com.example.bookings.Controller;

import com.example.bookings.DTO.BookingOrderDTO;
import com.example.bookings.DTO.BookingOrderDTO;
import com.example.bookings.DTO.SearchDTO;
import com.example.bookings.DTO.VacantHotelsDTO;
import com.example.bookings.Exceptions.BookingNotPossibleException;
import com.example.bookings.Models.Bookings;
import com.example.bookings.Repo.BookingRepo;
import com.example.bookings.Service.BookingService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/bookings/")
public class BookingController {

    @Autowired
    BookingService bookingService;

    @GetMapping("/_search/{hotelId}")
    public CompletionStage<ResponseEntity<List<Bookings>>> getBookingsForHotel(@PathVariable Long hotelId){
        return bookingService.getBookingsForHotel(hotelId).thenApplyAsync(res->new ResponseEntity<List<Bookings>>(res,HttpStatus.OK));
    }

//    @GetMapping("/user/{userId}")
//    public List<Bookings> getBookingsForAUser(@PathVariable Long userId){
//        return null; /*TODO*/
//    }

    @PostMapping("/_search/vacantHotels")
    public CompletionStage<ResponseEntity<Map<String, List<VacantHotelsDTO>>>> getVacantHotels(@RequestBody SearchDTO hotelSearchDTO) throws IOException {
        return bookingService.getBookedRooms(hotelSearchDTO).thenApplyAsync(res->new ResponseEntity<Map<String, List<VacantHotelsDTO>>>(res,HttpStatus.OK));
    }

    @PostMapping("/_order")
    @RabbitListener(queues = {"q.room-booking"}, containerFactory = "bookingListenerContainerFactory")
    public CompletionStage<ResponseEntity<Map<String,Object>>> createBooking(@RequestBody String bookingMessage) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        BookingOrderDTO booking = mapper.readValue(bookingMessage, BookingOrderDTO.class);
        try{
            return bookingService.createBooking(booking).thenApplyAsync(res->new ResponseEntity<Map<String,Object>>(res,HttpStatus.OK));
        } catch (BookingNotPossibleException e){
            Map<String, Object> err_res = new HashMap<>();
            err_res.put("status", "FAILED");
            err_res.put("message", e.getMessage());
            return CompletableFuture.completedFuture(new ResponseEntity<Map<String,Object>>(err_res, HttpStatus.GATEWAY_TIMEOUT));
        }
    }

    @PatchMapping("/_order/{id}")
    public CompletionStage<ResponseEntity<Bookings>> updateBooking(@RequestBody Bookings booking, @PathVariable Long id){
        return bookingService.updateBooking(id, booking).thenApplyAsync(res->new ResponseEntity<Bookings>(res,HttpStatus.OK));
    }

    @GetMapping("/_status/update/{bookingReferenceID}")
    public CompletionStage<ResponseEntity<Map<String, String>>> updateBooking(@PathVariable String bookingReferenceID, @RequestParam String status) throws BookingNotPossibleException {
        try{
            if(status.equals("ACTIVE")){
                bookingService.sendUpdateToUserUponSuccess(bookingReferenceID);
            }
            return bookingService.updateBookingStatus(bookingReferenceID, status).thenApplyAsync(res->new ResponseEntity<Map<String, String>>(res,HttpStatus.OK));
        } catch (BookingNotPossibleException e){
            Map<String, String> res = new HashMap<>();
            res.put("status", "FAILED");
            res.put("error_message", e.getMessage());
            return CompletableFuture.completedFuture(new ResponseEntity<>(res, HttpStatus.NOT_FOUND));
        }
    }

    @DeleteMapping("/_delete/{bookingId}")
    public CompletionStage<ResponseEntity<Object>> deleteBooking(@PathVariable Long bookingId){
        return bookingService.deleteBooking(bookingId).thenApplyAsync(res-> new ResponseEntity<Object>(res, HttpStatus.OK));
    }

    @PostMapping("/_delete")
    public CompletionStage<ResponseEntity<Map<String,Object>>> bookingDeleteBulk(@RequestBody Map<String, List<String>> bookingRedIds) throws BookingNotPossibleException {
        try{
            return bookingService.deleteBookingBulk(bookingRedIds.get("refIds")).thenApplyAsync(res-> new ResponseEntity<Map<String,Object>>(res, HttpStatus.OK));
        } catch (BookingNotPossibleException e){
            Map<String, Object> res = new HashMap<>();
            res.put("status", "FAILED");
            res.put("error_message", e.getMessage());
            return CompletableFuture.completedFuture(new ResponseEntity<>(res, HttpStatus.NOT_FOUND));
        }
    }

    @PostMapping("/internal/_delete")
    public CompletionStage<ResponseEntity<Map<String,Object>>> bookingDeleteBulkScheduled() throws BookingNotPossibleException {
        try{
            return bookingService.deleteBookingInBulkScheduled().thenApplyAsync(res-> new ResponseEntity<Map<String,Object>>(res, HttpStatus.OK));
        } catch (BookingNotPossibleException e){
            Map<String, Object> res = new HashMap<>();
            res.put("status", "FAILED");
            res.put("error_message", e.getMessage());
            return CompletableFuture.completedFuture(new ResponseEntity<>(res, HttpStatus.NOT_FOUND));
        }
    }

}
