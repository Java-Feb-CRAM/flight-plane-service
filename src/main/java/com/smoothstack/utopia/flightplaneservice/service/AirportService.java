package com.smoothstack.utopia.flightplaneservice.service;

import com.smoothstack.utopia.flightplaneservice.dao.AirportDao;
import com.smoothstack.utopia.flightplaneservice.dto.CreateAirportDto;
import com.smoothstack.utopia.flightplaneservice.dto.UpdateAirportDto;
import com.smoothstack.utopia.flightplaneservice.exception.AirportDeletionNotAllowedException;
import com.smoothstack.utopia.flightplaneservice.exception.AirportNotFoundException;
import com.smoothstack.utopia.flightplaneservice.exception.DuplicateAirportException;
import com.smoothstack.utopia.shared.model.Airport;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Rob Maes
 * Mar 17 2021
 */
@Service
public class AirportService {

  private final AirportDao airportDao;

  @Autowired
  public AirportService(AirportDao airportDao) {
    this.airportDao = airportDao;
  }

  public List<Airport> getAllAirports() {
    return airportDao.findAll();
  }

  public Airport getAirport(String airportId) {
    return airportDao
      .findById(airportId)
      .orElseThrow(AirportNotFoundException::new);
  }

  public Airport createAirport(CreateAirportDto createAirportDto) {
    // if an airport with this id already exists, throw an exception
    airportDao
      .findById(createAirportDto.getIataId())
      .ifPresent(
        s -> {
          throw new DuplicateAirportException();
        }
      );
    // create a new airport
    Airport airport = new Airport();
    airport.setIataId(createAirportDto.getIataId());
    airport.setCity(createAirportDto.getCity());
    // save the new airport
    airportDao.save(airport);
    return airport;
  }

  public void updateAirport(
    String airportId,
    UpdateAirportDto updateAirportDto
  ) {
    // try to find the airport to update
    // if it does not exist, throw an error
    Airport airport = airportDao
      .findById(airportId)
      .orElseThrow(AirportNotFoundException::new);
    // if the user provided a new city, update the airport's city
    updateAirportDto.getCity().ifPresent(airport::setCity);
    // save the airport
    airportDao.save(airport);
  }

  public void deleteAirport(String airportId) {
    // try to find the airport to delete
    // if it does not exist, throw an exception
    Airport airport = airportDao
      .findById(airportId)
      .orElseThrow(AirportNotFoundException::new);
    // if the airport has associated routes, throw an exception
    if (
      !airport.getArrivals().isEmpty() || !airport.getDepartures().isEmpty()
    ) {
      throw new AirportDeletionNotAllowedException();
    }
    // delete the airport
    airportDao.delete(airport);
  }
}
