package com.example.jwt.service;

import com.example.jwt.model.Booking;
import com.example.jwt.model.BookingStatus;
import com.example.jwt.model.Flight;
import com.example.jwt.repository.BookingRepository;
import com.example.jwt.repository.FlightRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookingService {

  private final BookingRepository bookingRepo;
  private final FlightRepository flightRepo;

  public BookingService(BookingRepository bookingRepo, FlightRepository flightRepo) {
    this.bookingRepo = bookingRepo;
    this.flightRepo = flightRepo;
  }

  @Transactional
  public Booking createBooking(Booking booking) {
    // Validate flight exists and has seats
    Flight flight = flightRepo.findById(booking.getFlightId())
        .orElseThrow(() -> new RuntimeException("Flight not found with id: " + booking.getFlightId()));

    if (flight.getAvailableSeats() <= 0) {
      throw new RuntimeException("No seats available on this flight");
    }

    // Reduce available seats by 1 (since seatNumber suggests single seat booking)
    flight.setAvailableSeats(flight.getAvailableSeats() - 1);
    flightRepo.save(flight);

    booking.setBookingTime(LocalDateTime.now());
    booking.setStatus(BookingStatus.CONFIRMED);

    return bookingRepo.save(booking);
  }

  public List<Booking> getAllBookings() {
    return bookingRepo.findAll();
  }

  public Booking getBookingById(Long id) {
    return bookingRepo.findById(id).orElse(null);
  }

  public List<Booking> getBookingsByPassengerId(Long passengerId) {
    return bookingRepo.findByPassengerId(passengerId);
  }

  public List<Booking> getBookingsByFlightId(Long flightId) {
    return bookingRepo.findByFlightId(flightId);
  }

  @Transactional
  public void cancelBooking(Long bookingId) {
    Booking booking = bookingRepo.findById(bookingId)
        .orElseThrow(() -> new RuntimeException("Booking not found"));

    // Restore seat to flight
    Flight flight = flightRepo.findById(booking.getFlightId())
        .orElseThrow(() -> new RuntimeException("Flight not found"));

    flight.setAvailableSeats(flight.getAvailableSeats() + 1);
    flightRepo.save(flight);

    booking.setStatus(BookingStatus.CANCELLED);
    bookingRepo.save(booking);
  }
}