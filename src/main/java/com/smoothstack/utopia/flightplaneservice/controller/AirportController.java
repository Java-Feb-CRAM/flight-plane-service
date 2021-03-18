package com.smoothstack.utopia.flightplaneservice.controller;

import com.smoothstack.utopia.flightplaneservice.dto.CreateAirportDto;
import com.smoothstack.utopia.flightplaneservice.dto.UpdateAirportDto;
import com.smoothstack.utopia.flightplaneservice.service.AirportService;
import com.smoothstack.utopia.shared.model.Airport;
import java.util.List;
import javax.validation.Valid;
import javax.ws.rs.Path;
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
 * Mar 17 2021
 */
@RestController
@RequestMapping("/airports")
public class AirportController {

  private final AirportService airportService;

  @Autowired
  public AirportController(AirportService airportService) {
    this.airportService = airportService;
  }

  @GetMapping
  public List<Airport> getAllAirports() {
    return airportService.getAllAirports();
  }

  @GetMapping(path = "{airportId}")
  public Airport getAirport(@PathVariable("airportId") String airportId) {
    return airportService.getAirport(airportId);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public void createAirport(
    @Valid @RequestBody CreateAirportDto createAirportDto
  ) {
    airportService.createAirport(createAirportDto);
  }

  @PutMapping(path = "{airportId}")
  public void updateAirport(
    @PathVariable("airportId") String airportId,
    @Valid @RequestBody UpdateAirportDto updateAirportDto
  ) {
    airportService.updateAirport(airportId, updateAirportDto);
  }

  @DeleteMapping(path = "{airportId}")
  public void deleteAirport(@PathVariable("airportId") String airportId) {
    airportService.deleteAirport(airportId);
  }
}
