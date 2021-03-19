package com.smoothstack.utopia.flightplaneservice.controller;

import com.netflix.discovery.converters.Auto;
import com.smoothstack.utopia.flightplaneservice.dto.CreateRouteDto;
import com.smoothstack.utopia.flightplaneservice.dto.UpdateRouteDto;
import com.smoothstack.utopia.flightplaneservice.service.RouteService;
import com.smoothstack.utopia.shared.model.Route;
import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
@RequestMapping(
  path = "/routes",
  produces = {
    MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE,
  }
)
public class RouteController {

  private final RouteService routeService;

  @Autowired
  public RouteController(RouteService routeService) {
    this.routeService = routeService;
  }

  @GetMapping
  public List<Route> getAllRoutes() {
    return routeService.getAllRoutes();
  }

  @GetMapping(path = "{routeId}")
  public Route getRoute(@PathVariable("routeId") Long routeId) {
    return routeService.getRoute(routeId);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Route createRoute(@Valid @RequestBody CreateRouteDto createRouteDto) {
    return routeService.createRoute(createRouteDto);
  }

  @PutMapping(path = "{routeId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void updateRoute(
    @PathVariable("routeId") Long routeId,
    @Valid @RequestBody UpdateRouteDto updateRouteDto
  ) {
    routeService.updateRoute(routeId, updateRouteDto);
  }

  @DeleteMapping(path = "{routeId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteRoute(@PathVariable("routeId") Long routeId) {
    routeService.deleteRoute(routeId);
  }
}
