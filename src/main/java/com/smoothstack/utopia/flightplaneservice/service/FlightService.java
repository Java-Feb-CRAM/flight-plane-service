package com.smoothstack.utopia.flightplaneservice.service;

import com.smoothstack.utopia.flightplaneservice.dao.FlightDao;
import com.smoothstack.utopia.flightplaneservice.dto.CreateFlightDto;
import com.smoothstack.utopia.flightplaneservice.dto.UpdateFlightDto;
import com.smoothstack.utopia.flightplaneservice.exception.FlightNotFoundException;
import com.smoothstack.utopia.shared.model.Flight;
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

  @Autowired
  public FlightService(FlightDao flightDao) {
    this.flightDao = flightDao;
  }

  public List<Flight> getAllFlights() {
    return flightDao.findAll();
  }

  public Flight getFlight(Long flightId) {
    return flightDao
      .findById(flightId)
      .orElseThrow(FlightNotFoundException::new);
  }

  public void createFlight(CreateFlightDto createFlightDto) {}

  public void updateFlight(Long flightId, UpdateFlightDto updateFlightDto) {}

  public void deleteFlight(Long flightId) {}
}
