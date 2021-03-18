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
@RequestMapping("/routes")
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
  public void createRoute(@Valid @RequestBody CreateRouteDto createRouteDto) {
    routeService.createRoute(createRouteDto);
  }

  @PutMapping(path = "{routeId}")
  public void updateRoute(
    @PathVariable("routeId") Long routeId,
    @Valid @RequestBody UpdateRouteDto updateRouteDto
  ) {
    routeService.updateRoute(routeId, updateRouteDto);
  }

  @DeleteMapping(path = "{routeId}")
  public void deleteRoute(@PathVariable("routeId") Long routeId) {
    routeService.deleteRoute(routeId);
  }
}
