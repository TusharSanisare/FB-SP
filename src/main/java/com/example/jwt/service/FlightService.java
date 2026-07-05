package com.example.jwt.service;

import com.example.jwt.model.Flight;
import com.example.jwt.repository.FlightRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FlightService {

  private final FlightRepository flightRepo;

  public FlightService(FlightRepository flightRepo) {
    this.flightRepo = flightRepo;
  }

  public List<Flight> getAllFlights() {
    return flightRepo.findAll();
  }

  public List<Flight> getFlightsByDeparture(String departure) {
    return flightRepo.findByDeparture(departure);
  }

  public List<Flight> getFlightsByDestination(String destination) {
    return flightRepo.findByDestination(destination);
  }

  public List<Flight> getFlightsByDepartureAndDestination(String departure, String destination) {
    return flightRepo.findByDepartureAndDestination(departure, destination);
  }

  public Flight getFlightById(Long flightId) {
    return flightRepo.findById(flightId).orElse(null);
  }

  public Flight addFlight(Flight flight) {
    return flightRepo.save(flight);
  }

  public Flight updateFlight(Flight flight) {
    return flightRepo.save(flight);
  }

  public void deleteFlightById(Long flightId) {
    flightRepo.deleteById(flightId);
  }
}