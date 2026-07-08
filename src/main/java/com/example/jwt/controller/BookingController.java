package com.example.jwt.controller;

import com.example.jwt.model.Booking;
import com.example.jwt.service.BookingService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

  private final BookingService bookingService;

  public BookingController(BookingService bookingService) {
    this.bookingService = bookingService;
  }

  @PostMapping
  public ResponseEntity<Booking> createBooking(@Valid @RequestBody Booking booking) {
    Booking saved = bookingService.createBooking(booking);
    return ResponseEntity.status(HttpStatus.CREATED).body(saved);
  }

  @GetMapping
  @PreAuthorize("hasRole('ADMIN')")
  public List<Booking> getAllBookings() {
    return bookingService.getAllBookings();
  }

  @GetMapping("/{id}")
  public ResponseEntity<Booking> getBookingById(@PathVariable Long id) {
    Booking booking = bookingService.getBookingById(id);
    return booking != null ? ResponseEntity.ok(booking) : ResponseEntity.notFound().build();
  }

  @GetMapping("/passenger/{passengerId}")
  public List<Booking> getBookingsByPassenger(@PathVariable Long passengerId) {
    return bookingService.getBookingsByPassengerId(passengerId);
  }

  @GetMapping("/flight/{flightId}")
  public List<Booking> getBookingsByFlight(@PathVariable Long flightId) {
    return bookingService.getBookingsByFlightId(flightId);
  }

  @DeleteMapping("/{id}/cancel")
  public ResponseEntity<Void> cancelBooking(@PathVariable Long id) {
    bookingService.cancelBooking(id);
    return ResponseEntity.noContent().build();
  }
}

// @RestController
// @RequestMapping("/api/bookings")
// public class BookingController {

// private final BookingService bookingService;

// public BookingController(BookingService bookingService) {
// this.bookingService = bookingService;
// }

// @PostMapping
// public ResponseEntity<Booking> createBooking(@RequestBody Booking booking) {
// Booking saved = bookingService.createBooking(booking);
// return ResponseEntity.status(HttpStatus.CREATED).body(saved);
// }

// @GetMapping
// public List<Booking> getAllBookings() {
// return bookingService.getAllBookings();
// }

// @GetMapping("/{id}")
// public ResponseEntity<Booking> getBookingById(@PathVariable Long id) {
// Booking booking = bookingService.getBookingById(id);
// return booking != null ? ResponseEntity.ok(booking) :
// ResponseEntity.notFound().build();
// }

// @GetMapping("/passenger/{passengerId}")
// public List<Booking> getBookingsByPassenger(@PathVariable Long passengerId) {
// return bookingService.getBookingsByPassengerId(passengerId);
// }

// @GetMapping("/flight/{flightId}")
// public List<Booking> getBookingsByFlight(@PathVariable Long flightId) {
// return bookingService.getBookingsByFlightId(flightId);
// }

// @DeleteMapping("/{id}/cancel")
// public ResponseEntity<Void> cancelBooking(@PathVariable Long id) {
// bookingService.cancelBooking(id);
// return ResponseEntity.noContent().build();
// }
// }