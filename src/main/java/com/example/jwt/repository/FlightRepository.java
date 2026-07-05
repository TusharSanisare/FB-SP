package com.example.jwt.repository;

import com.example.jwt.model.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FlightRepository extends JpaRepository<Flight, Long> {

  List<Flight> findByDeparture(String departure);

  List<Flight> findByDestination(String destination);

  List<Flight> findByDepartureAndDestination(String departure, String destination);
}