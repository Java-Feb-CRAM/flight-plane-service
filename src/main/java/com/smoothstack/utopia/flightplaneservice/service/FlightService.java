package com.smoothstack.utopia.flightplaneservice.service;

import com.smoothstack.utopia.flightplaneservice.dao.AirplaneDao;
import com.smoothstack.utopia.flightplaneservice.dao.FlightDao;
import com.smoothstack.utopia.flightplaneservice.dao.RouteDao;
import com.smoothstack.utopia.flightplaneservice.dto.CreateFlightDto;
import com.smoothstack.utopia.flightplaneservice.dto.UpdateFlightDto;
import com.smoothstack.utopia.flightplaneservice.exception.AirplaneNotFoundException;
import com.smoothstack.utopia.flightplaneservice.exception.FlightDeletionNotAllowedException;
import com.smoothstack.utopia.flightplaneservice.exception.FlightNotFoundException;
import com.smoothstack.utopia.flightplaneservice.exception.RouteNotFoundException;
import com.smoothstack.utopia.shared.model.Airplane;
import com.smoothstack.utopia.shared.model.Flight;
import com.smoothstack.utopia.shared.model.Route;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Rob Maes
 * Mar 18 2021
 */
@Service
public class FlightService {

  private final FlightDao flightDao;
  private final RouteDao routeDao;
  private final AirplaneDao airplaneDao;

  @Autowired
  public FlightService(
    FlightDao flightDao,
    RouteDao routeDao,
    AirplaneDao airplaneDao
  ) {
    this.flightDao = flightDao;
    this.routeDao = routeDao;
    this.airplaneDao = airplaneDao;
  }

  public List<Flight> getAllFlights() {
    return flightDao.findAll();
  }

  public Flight getFlight(Long flightId) {
    return flightDao
      .findById(flightId)
      .orElseThrow(FlightNotFoundException::new);
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
