package com.smoothstack.utopia.flightplaneservice.service;

import com.smoothstack.utopia.flightplaneservice.dao.AirplaneDao;
import com.smoothstack.utopia.flightplaneservice.dao.AirportDao;
import com.smoothstack.utopia.flightplaneservice.dao.FlightDao;
import com.smoothstack.utopia.flightplaneservice.dao.RouteDao;
import com.smoothstack.utopia.flightplaneservice.dto.CreateFlightDto;
import com.smoothstack.utopia.flightplaneservice.dto.UpdateFlightDto;
import com.smoothstack.utopia.flightplaneservice.exception.AirplaneNotFoundException;
import com.smoothstack.utopia.flightplaneservice.exception.AirportNotFoundException;
import com.smoothstack.utopia.flightplaneservice.exception.FlightDeletionNotAllowedException;
import com.smoothstack.utopia.flightplaneservice.exception.FlightNotFoundException;
import com.smoothstack.utopia.flightplaneservice.exception.RouteNotFoundException;
import com.smoothstack.utopia.shared.model.Airplane;
import com.smoothstack.utopia.shared.model.Flight;
import com.smoothstack.utopia.shared.model.Route;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Rob Maes
 * Mar 18 2021
 * 
 * @editor Craig Saunders UT-60 flight search
 * 
 */
@Service
public class FlightService {

  private final FlightDao flightDao;
  private final RouteDao routeDao;
  private final AirplaneDao airplaneDao;
  private final AirportDao airportDao;

  @Autowired
  public FlightService(
    FlightDao flightDao,
    RouteDao routeDao,
    AirplaneDao airplaneDao,
    AirportDao airportDao
  ) {
    this.flightDao = flightDao;
    this.routeDao = routeDao;
    this.airplaneDao = airplaneDao;
    this.airportDao = airportDao;
  }

  public List<Flight> getAllFlights() {
    return flightDao.findAll();
  }

  public Flight getFlight(Long flightId) {
    return flightDao
      .findById(flightId)
      .orElseThrow(FlightNotFoundException::new);
  }
  
  public List<List<Flight>> getAllMultiStopFlights(String originIataId,
      String destinationIataId,
      final Integer stops, String firstOriginIataId)
  {
    if (stops > 0) {
      List<List<Flight>> pathsOfFlights = new ArrayList<List<Flight>>();
      Stream
          .of(getAllFlightsWithMatchingOriginAirportInFlightRoute(originIataId, firstOriginIataId))
          .forEach(originFlight ->
          {
            if (
              originFlight.getRoute().getDestinationAirport().getIataId()
                  .equals(destinationIataId)
            ) {
              pathsOfFlights.addAll(
                  getFlightPathDestinationFlights(
                      originIataId,
                      destinationIataId
                  )
              );
            } else {
              getAllMultiStopFlights(
                  originFlight.getRoute().getDestinationAirport().getIataId(),
                  destinationIataId,
                  stops - 1,
                  firstOriginIataId
              ).stream().forEach(returnedFlightPath ->
          {
                returnedFlightPath.add(0, originFlight);
                pathsOfFlights.add(returnedFlightPath);
              });
            }
          });
      return pathsOfFlights;
    } else {
      return getFlightPathDestinationFlights(originIataId, destinationIataId);
    }
  }

  public List<List<Flight>> getFlightPathDestinationFlights(String originIataId,
      String destinationIataId)
  {
    List<List<Flight>> pathsOfFlights = new ArrayList<List<Flight>>();
    List<Flight> path = new ArrayList<Flight>();
    Stream.of(getAllNoStopFlights(originIataId, destinationIataId))
        .forEach(flight ->
        {
          path.add(flight);
          pathsOfFlights.add(path);
        });
    return pathsOfFlights;
  }

  private Flight[] getAllNoStopFlights(String originIataId,
      String destinationIataId)
  {
    Optional<Route> route =
        routeDao.findRouteByOriginAirportAndDestinationAirport(
            airportDao.findByIataId(originIataId)
                .orElseThrow(AirportNotFoundException::new),
            airportDao.findByIataId(destinationIataId)
                .orElseThrow(AirportNotFoundException::new)
        );

    if (route.isPresent()) {
        return flightDao.findAllByRouteIdAndHasVacancy(route.get().getId());
    }
    return new Flight[0];
  }

  private Flight[]
      getAllFlightsWithMatchingOriginAirportInFlightRoute(String originIataId, String firstOriginIataId)
  {
    return Stream
        .of(
            routeDao.findAllRoutesByOriginAirport(
                airportDao.findByIataId(originIataId)
                    .orElseThrow(AirportNotFoundException::new)
            ).orElseThrow(RouteNotFoundException::new)
        ).filter(route -> !route.getDestinationAirport().getIataId().equals(firstOriginIataId))
        .map(
            route -> flightDao.findAllByRoute(route)
                .orElseThrow(FlightNotFoundException::new)
        ).flatMap(flights -> Stream.of(flights)).toArray(Flight[]::new);
  }
  
  public Flight createFlight(CreateFlightDto createFlightDto) {
    Route route = routeDao
      .findById(createFlightDto.getRouteId())
      .orElseThrow(RouteNotFoundException::new);
    Airplane airplane = airplaneDao
      .findById(createFlightDto.getAirplaneId())
      .orElseThrow(AirplaneNotFoundException::new);
    Flight flight = new Flight();
    flight.setRoute(route);
    flight.setAirplane(airplane);
    flight.setDepartureTime(createFlightDto.getDepartureTime());
    flight.setReservedSeats(createFlightDto.getReservedSeats());
    flight.setSeatPrice(createFlightDto.getSeatPrice());
    flightDao.save(flight);
    return flight;
  }

  public void updateFlight(Long flightId, UpdateFlightDto updateFlightDto) {
    Flight flight = flightDao
      .findById(flightId)
      .orElseThrow(FlightNotFoundException::new);
    updateFlightDto
      .getAirplaneId()
      .ifPresent(
        airplaneId -> {
          Airplane airplane = airplaneDao
            .findById(airplaneId)
            .orElseThrow(AirplaneNotFoundException::new);
          flight.setAirplane(airplane);
        }
      );
    updateFlightDto
      .getRouteId()
      .ifPresent(
        routeId -> {
          Route route = routeDao
            .findById(routeId)
            .orElseThrow(RouteNotFoundException::new);
          flight.setRoute(route);
        }
      );
    updateFlightDto.getReservedSeats().ifPresent(flight::setReservedSeats);
    updateFlightDto.getDepartureTime().ifPresent(flight::setDepartureTime);
    updateFlightDto.getSeatPrice().ifPresent(flight::setSeatPrice);
    flightDao.save(flight);
  }

  public void deleteFlight(Long flightId) {
    Flight flight = flightDao
      .findById(flightId)
      .orElseThrow(FlightNotFoundException::new);
    if (!flight.getBookings().isEmpty()) {
      throw new FlightDeletionNotAllowedException();
    }
    flightDao.delete(flight);
  }
}
