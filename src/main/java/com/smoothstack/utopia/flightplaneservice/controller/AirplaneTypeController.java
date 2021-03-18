package com.smoothstack.utopia.flightplaneservice.controller;

import com.smoothstack.utopia.flightplaneservice.dto.CreateAirplaneTypeDto;
import com.smoothstack.utopia.flightplaneservice.dto.UpdateAirplaneTypeDto;
import com.smoothstack.utopia.flightplaneservice.service.AirplaneTypeService;
import com.smoothstack.utopia.shared.model.AirplaneType;
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
 * Mar 17 2021
 */
@RestController
@RequestMapping("/airplane_types")
public class AirplaneTypeController {

  private final AirplaneTypeService airplaneTypeService;

  @Autowired
  public AirplaneTypeController(AirplaneTypeService airplaneTypeService) {
    this.airplaneTypeService = airplaneTypeService;
  }

  @GetMapping
  public List<AirplaneType> getAllAirplaneTypes() {
    return airplaneTypeService.getAllAirplaneTypes();
  }

  @GetMapping(path = "{airplaneTypeId}")
  public AirplaneType getAirplaneType(
    @PathVariable("airplaneTypeId") Long airplaneTypeId
  ) {
    return airplaneTypeService.getAirplaneType(airplaneTypeId);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public void createAirplaneType(
    @Valid @RequestBody CreateAirplaneTypeDto createAirplaneTypeDto
  ) {
    airplaneTypeService.createAirplaneType(createAirplaneTypeDto);
  }

  @PutMapping(path = "{airplaneTypeId}")
  public void updateAirplaneType(
    @PathVariable("airplaneTypeId") Long airplaneTypeId,
    @Valid @RequestBody UpdateAirplaneTypeDto updateAirplaneTypeDto
  ) {
    airplaneTypeService.updateAirplaneType(
      airplaneTypeId,
      updateAirplaneTypeDto
    );
  }

  @DeleteMapping(path = "{airplaneTypeId}")
  public void deleteAirplaneTypeId(
    @PathVariable("airplaneTypeId") Long airplaneTypeId
  ) {
    airplaneTypeService.deleteAirplaneType(airplaneTypeId);
  }
}
