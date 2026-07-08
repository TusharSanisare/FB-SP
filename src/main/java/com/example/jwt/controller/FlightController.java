package com.example.jwt.controller;

import com.example.jwt.model.Flight;
import com.example.jwt.service.FlightService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/flights")
public class FlightController {

  private final FlightService flightService;

  public FlightController(FlightService flightService) {
    this.flightService = flightService;
  }

  @GetMapping
  public List<Flight> getAllFlights() {
    return flightService.getAllFlights();
  }

  @GetMapping("/departure")
  public List<Flight> getFlightsByDeparture(@RequestParam String departure) {
    return flightService.getFlightsByDeparture(departure);
  }

  @GetMapping("/destination")
  public List<Flight> getFlightsByDestination(@RequestParam String destination) {
    return flightService.getFlightsByDestination(destination);
  }

  @GetMapping("/search")
  public List<Flight> searchFlights(
      @RequestParam String departure,
      @RequestParam String destination) {
    return flightService.getFlightsByDepartureAndDestination(departure, destination);
  }

  @GetMapping("/{id}")
  public ResponseEntity<Flight> getFlightById(@PathVariable Long id) {
    Flight flight = flightService.getFlightById(id);
    return flight != null ? ResponseEntity.ok(flight) : ResponseEntity.notFound().build();
  }

  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Flight> addFlight(@Valid @RequestBody Flight flight) {
    Flight savedFlight = flightService.addFlight(flight);
    return ResponseEntity.status(HttpStatus.CREATED).body(savedFlight);
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Flight> updateFlight(@PathVariable Long id, @Valid @RequestBody Flight flight) {
    Flight existing = flightService.getFlightById(id);
    if (existing == null) {
      return ResponseEntity.notFound().build();
    }
    flight.setId(id); // Ensure ID is set, ignoring any id the client sent in the body
    Flight updatedFlight = flightService.updateFlight(flight);
    return ResponseEntity.ok(updatedFlight);
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Void> deleteFlight(@PathVariable Long id) {
    flightService.deleteFlightById(id);
    return ResponseEntity.noContent().build();
  }
}

// @RestController
// @RequestMapping("/api/flights")
// public class FlightController {

// private final FlightService flightService;

// public FlightController(FlightService flightService) {
// this.flightService = flightService;
// }

// @GetMapping
// public List<Flight> getAllFlights() {
// return flightService.getAllFlights();
// }

// @GetMapping("/departure")
// public List<Flight> getFlightsByDeparture(@RequestParam String departure) {
// return flightService.getFlightsByDeparture(departure);
// }

// @GetMapping("/destination")
// public List<Flight> getFlightsByDestination(@RequestParam String destination)
// {
// return flightService.getFlightsByDestination(destination);
// }

// @GetMapping("/search")
// public List<Flight> searchFlights(
// @RequestParam String departure,
// @RequestParam String destination) {
// return flightService.getFlightsByDepartureAndDestination(departure,
// destination);
// }

// @GetMapping("/{id}")
// public ResponseEntity<Flight> getFlightById(@PathVariable Long id) {
// Flight flight = flightService.getFlightById(id);
// return flight != null ? ResponseEntity.ok(flight) :
// ResponseEntity.notFound().build();
// }

// @PostMapping
// public ResponseEntity<Flight> addFlight(@RequestBody Flight flight) {
// Flight savedFlight = flightService.addFlight(flight);
// return ResponseEntity.status(HttpStatus.CREATED).body(savedFlight);
// }

// @PutMapping("/{id}")
// public ResponseEntity<Flight> updateFlight(@PathVariable Long id,
// @RequestBody Flight flight) {
// flight.setId(id); // Ensure ID is set
// Flight updatedFlight = flightService.updateFlight(flight);
// return ResponseEntity.ok(updatedFlight);
// }

// @DeleteMapping("/{id}")
// public ResponseEntity<Void> deleteFlight(@PathVariable Long id) {
// flightService.deleteFlightById(id);
// return ResponseEntity.noContent().build();
// }
// }