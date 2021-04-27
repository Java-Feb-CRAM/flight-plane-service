package com.smoothstack.utopia.flightplaneservice.service;

import com.smoothstack.utopia.flightplaneservice.dao.AirportDao;
import com.smoothstack.utopia.flightplaneservice.dao.RouteDao;
import com.smoothstack.utopia.flightplaneservice.dto.CreateRouteDto;
import com.smoothstack.utopia.flightplaneservice.dto.UpdateRouteDto;
import com.smoothstack.utopia.flightplaneservice.exception.AirportNotFoundException;
import com.smoothstack.utopia.flightplaneservice.exception.DuplicateRouteException;
import com.smoothstack.utopia.flightplaneservice.exception.InvalidRouteException;
import com.smoothstack.utopia.flightplaneservice.exception.RouteDeletionNotAllowedException;
import com.smoothstack.utopia.flightplaneservice.exception.RouteNotFoundException;
import com.smoothstack.utopia.shared.model.Airport;
import com.smoothstack.utopia.shared.model.Route;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Rob Maes
 * Mar 17 2021
 */
@Service
public class RouteService {

  private final RouteDao routeDao;
  private final AirportDao airportDao;

  @Autowired
  public RouteService(RouteDao routeDao, AirportDao airportDao) {
    this.routeDao = routeDao;
    this.airportDao = airportDao;
  }

  public List<Route> getAllRoutes() {
    return routeDao.findAll();
  }

  public Route getRoute(Long routeId) {
    return routeDao.findById(routeId).orElseThrow(RouteNotFoundException::new);
  }

  public Route createRoute(CreateRouteDto createRouteDto) {
    Airport originAirport = airportDao
      .findById(createRouteDto.getOriginAirportId())
      .orElseThrow(AirportNotFoundException::new);
    Airport destinationAirport = airportDao
      .findById(createRouteDto.getDestinationAirportId())
      .orElseThrow(AirportNotFoundException::new);
    if (originAirport.getIataId().equals(destinationAirport.getIataId())) {
      throw new InvalidRouteException();
    }
    routeDao
      .findRouteByOriginAirportAndDestinationAirport(
        originAirport,
        destinationAirport
      )
      .ifPresent(
        s -> {
          throw new DuplicateRouteException();
        }
      );
    Route route = new Route(originAirport, destinationAirport);
    routeDao.save(route);
    return route;
  }

  public void updateRoute(Long routeId, UpdateRouteDto updateRouteDto) {
    Route route = routeDao
      .findById(routeId)
      .orElseThrow(RouteNotFoundException::new);
    updateRouteDto
      .getOriginAirportId()
      .ifPresent(
        originAirportId -> {
          Airport originAirport = airportDao
            .findById(originAirportId)
            .orElseThrow(AirportNotFoundException::new);
          route.setOriginAirport(originAirport);
        }
      );
    updateRouteDto
      .getDestinationAirportId()
      .ifPresent(
        destinationAirportId -> {
          Airport destinationAirport = airportDao
            .findById(destinationAirportId)
            .orElseThrow(AirportNotFoundException::new);
          route.setDestinationAirport(destinationAirport);
        }
      );
    if (
      route
        .getOriginAirport()
        .getIataId()
        .equals(route.getDestinationAirport().getIataId())
    ) {
      throw new InvalidRouteException();
    }
    routeDao
      .findRouteByOriginAirportAndDestinationAirport(
        route.getOriginAirport(),
        route.getDestinationAirport()
      )
      .ifPresent(
        s -> {
          throw new DuplicateRouteException();
        }
      );
    routeDao.save(route);
  }

  public void deleteRoute(Long routeId) {
    Route route = routeDao
      .findById(routeId)
      .orElseThrow(RouteNotFoundException::new);
    if (!route.getFlights().isEmpty()) {
      throw new RouteDeletionNotAllowedException();
    }
    routeDao.delete(route);
  }
}
