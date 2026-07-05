package com.example.jwt.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
public class Booking {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull(message = "Passenger ID is required")
  private Long passengerId;

  @NotNull(message = "Flight ID is required")
  private Long flightId;

  private String seatNumber;

  private LocalDateTime bookingTime;

  @Enumerated(EnumType.STRING)
  private BookingStatus status;

  // Constructors
  public Booking() {
    this.bookingTime = LocalDateTime.now();
    this.status = BookingStatus.CONFIRMED;
  }

  // Getters and Setters
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getPassengerId() {
    return passengerId;
  }

  public void setPassengerId(Long passengerId) {
    this.passengerId = passengerId;
  }

  public Long getFlightId() {
    return flightId;
  }

  public void setFlightId(Long flightId) {
    this.flightId = flightId;
  }

  public String getSeatNumber() {
    return seatNumber;
  }

  public void setSeatNumber(String seatNumber) {
    this.seatNumber = seatNumber;
  }

  public LocalDateTime getBookingTime() {
    return bookingTime;
  }

  public void setBookingTime(LocalDateTime bookingTime) {
    this.bookingTime = bookingTime;
  }

  public BookingStatus getStatus() {
    return status;
  }

  public void setStatus(BookingStatus status) {
    this.status = status;
  }
}