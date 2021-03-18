package com.smoothstack.utopia.flightplaneservice.controller;

import com.smoothstack.utopia.flightplaneservice.dto.CreateFlightDto;
import com.smoothstack.utopia.flightplaneservice.dto.UpdateFlightDto;
import com.smoothstack.utopia.flightplaneservice.service.FlightService;
import com.smoothstack.utopia.shared.model.Flight;
import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Rob Maes
 * Mar 18 2021
 */
@RestController
@RequestMapping("/flights")
public class FlightController {

  private final FlightService flightService;

  @Autowired
  public FlightController(FlightService flightService) {
    this.flightService = flightService;
  }

  @GetMapping
  public List<Flight> getAllFlights() {
    return flightService.getAllFlights();
  }

  @GetMapping(path = "{flightId}")
  public Flight getFlight(@PathVariable("flightId") Long flightId) {
    return flightService.getFlight(flightId);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public void createFlight(
    @Valid @RequestBody CreateFlightDto createFlightDto
  ) {
    flightService.createFlight(createFlightDto);
  }

  @PutMapping(path = "{flightId}")
  public void updateFlight(
    @PathVariable("flightId") Long flightId,
    @Valid @RequestBody UpdateFlightDto updateFlightDto
  ) {
    flightService.updateFlight(flightId, updateFlightDto);
  }

  @DeleteMapping(path = "{flightId}")
  public void deleteFlight(@PathVariable("flightId") Long flightId) {
    flightService.deleteFlight(flightId);
  }
}
