package com.smoothstack.utopia.flightplaneservice.controller;

import com.smoothstack.utopia.flightplaneservice.dto.CreateAirplaneDto;
import com.smoothstack.utopia.flightplaneservice.dto.UpdateAirplaneDto;
import com.smoothstack.utopia.flightplaneservice.service.AirplaneService;
import com.smoothstack.utopia.shared.model.Airplane;
import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
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
@RequestMapping(
  path = "/airplanes",
  produces = {
    MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE,
  }
)
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
  public Airplane createAirplane(
    @Valid @RequestBody CreateAirplaneDto createAirplaneDto
  ) {
    return airplaneService.createAirplane(createAirplaneDto);
  }

  @PutMapping(path = "{airplaneId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void updateAirplane(
    @PathVariable("airplaneId") Long airplaneId,
    @Valid @RequestBody UpdateAirplaneDto updateAirplaneDto
  ) {
    airplaneService.updateAirplane(airplaneId, updateAirplaneDto);
  }

  @DeleteMapping(path = "{airplaneId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteAirplane(@PathVariable("airplaneId") Long airplaneId) {
    airplaneService.deleteAirplane(airplaneId);
  }
}
