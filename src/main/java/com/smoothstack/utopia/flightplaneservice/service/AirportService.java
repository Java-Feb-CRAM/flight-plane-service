package com.smoothstack.utopia.flightplaneservice.service;

import com.smoothstack.utopia.flightplaneservice.dao.AirportDao;
import com.smoothstack.utopia.flightplaneservice.dto.CreateAirportDto;
import com.smoothstack.utopia.flightplaneservice.dto.UpdateAirportDto;
import com.smoothstack.utopia.flightplaneservice.exception.AirportNotFoundException;
import com.smoothstack.utopia.flightplaneservice.exception.DuplicateAirportException;
import com.smoothstack.utopia.shared.model.Airport;
import java.util.List;
import javax.transaction.Transactional;
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

  public void createAirport(CreateAirportDto createAirportDto) {
    airportDao
      .findById(createAirportDto.getId())
      .ifPresent(
        s -> {
          throw new DuplicateAirportException();
        }
      );
    Airport airport = new Airport();
    airport.setIataId(createAirportDto.getId());
    airport.setCity(createAirportDto.getCity());
    airportDao.save(airport);
  }

  //  @Transactional
  public void updateAirport(
    String airportId,
    UpdateAirportDto updateAirportDto
  ) {
    //TODO: implement updateAirport
  }

  public void deleteAirport(String airportId) {
    //TODO: implement deleteAirport
  }
}
