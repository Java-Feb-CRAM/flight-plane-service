package com.smoothstack.utopia.flightplaneservice.controller;

import com.netflix.discovery.converters.Auto;
import com.smoothstack.utopia.flightplaneservice.dto.CreateSeatLayoutDto;
import com.smoothstack.utopia.flightplaneservice.service.SeatLayoutService;
import com.smoothstack.utopia.shared.model.SeatLayout;
import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Rob Maes
 * May 10 2021
 */
@RestController
@RequestMapping(
  path = "/seat_layouts",
  produces = {
    MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE,
  }
)
public class SeatLayoutController {

  private final SeatLayoutService seatLayoutService;

  @Autowired
  public SeatLayoutController(SeatLayoutService seatLayoutService) {
    this.seatLayoutService = seatLayoutService;
  }

  @GetMapping
  public List<SeatLayout> getAllSeatLayouts() {
    return seatLayoutService.getAllSeatLayouts();
  }

  @GetMapping(path = "{seatLayoutId}")
  public SeatLayout getSeatLayout(
    @PathVariable("seatLayoutId") Long seatLayoutId
  ) {
    return seatLayoutService.getSeatLayout(seatLayoutId);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public SeatLayout createSeatLayout(
    @Valid @RequestBody CreateSeatLayoutDto createSeatLayoutDto
  ) {
    return seatLayoutService.createSeatLayout(createSeatLayoutDto);
  }

  @DeleteMapping(path = "{seatLayoutId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteSeatLayout(
    @PathVariable("seatLayoutId") Long seatLayoutId
  ) {
    seatLayoutService.deleteSeatLayout(seatLayoutId);
  }
}
