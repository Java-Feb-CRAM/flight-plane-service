package com.smoothstack.utopia.flightplaneservice.controller;

import com.netflix.discovery.converters.Auto;
import com.smoothstack.utopia.flightplaneservice.dto.CreateAirplaneDto;
import com.smoothstack.utopia.flightplaneservice.dto.UpdateAirplaneDto;
import com.smoothstack.utopia.flightplaneservice.service.AirplaneService;
import com.smoothstack.utopia.shared.model.Airplane;
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
@RequestMapping("/airplanes")
public class AirplaneController {

  private final AirplaneService airplaneService;

  @Autowired
  public AirplaneController(AirplaneService airplaneService) {
    this.airplaneService = airplaneService;
  }

  @GetMapping
  public List<Airplane> getAllAirplanes() {
    return airplaneService.getAllAirplanes();
  }

  @GetMapping(path = "{airplaneId}")
  public Airplane getAirplane(@PathVariable("airplaneId") Long airplaneId) {
    return airplaneService.getAirplane(airplaneId);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public void createAirplane(
    @Valid @RequestBody CreateAirplaneDto createAirplaneDto
  ) {
    airplaneService.createAirplane(createAirplaneDto);
  }

  @PutMapping(path = "{airplaneId}")
  public void updateAirplane(
    @PathVariable("airplaneId") Long airplaneId,
    @Valid @RequestBody UpdateAirplaneDto updateAirplaneDto
  ) {
    airplaneService.updateAirplane(airplaneId, updateAirplaneDto);
  }

  @DeleteMapping(path = "{airplaneId}")
  public void deleteAirplane(@PathVariable("airplaneId") Long airplaneId) {
    airplaneService.deleteAirplane(airplaneId);
  }
}
