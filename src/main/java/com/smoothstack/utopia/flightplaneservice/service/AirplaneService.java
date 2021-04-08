package com.smoothstack.utopia.flightplaneservice.service;

import com.smoothstack.utopia.flightplaneservice.dao.AirplaneDao;
import com.smoothstack.utopia.flightplaneservice.dao.AirplaneTypeDao;
import com.smoothstack.utopia.flightplaneservice.dto.CreateAirplaneDto;
import com.smoothstack.utopia.flightplaneservice.dto.UpdateAirplaneDto;
import com.smoothstack.utopia.flightplaneservice.exception.AirplaneDeletionNotAllowedException;
import com.smoothstack.utopia.flightplaneservice.exception.AirplaneNotFoundException;
import com.smoothstack.utopia.flightplaneservice.exception.AirplaneTypeNotFoundException;
import com.smoothstack.utopia.shared.model.Airplane;
import com.smoothstack.utopia.shared.model.AirplaneType;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Rob Maes
 * Mar 18 2021
 */
@Service
public class AirplaneService {

  private final AirplaneDao airplaneDao;
  private final AirplaneTypeDao airplaneTypeDao;

  @Autowired
  public AirplaneService(
    AirplaneDao airplaneDao,
    AirplaneTypeDao airplaneTypeDao
  ) {
    this.airplaneDao = airplaneDao;
    this.airplaneTypeDao = airplaneTypeDao;
  }

  public List<Airplane> getAllAirplanes() {
    return airplaneDao.findAll();
  }

  public Airplane getAirplane(Long airplaneId) {
    return airplaneDao
      .findById(airplaneId)
      .orElseThrow(AirplaneNotFoundException::new);
  }

  public Airplane createAirplane(CreateAirplaneDto createAirplaneDto) {
    AirplaneType airplaneType = airplaneTypeDao
      .findById(createAirplaneDto.getAirplaneTypeId())
      .orElseThrow(AirplaneTypeNotFoundException::new);
    Airplane airplane = new Airplane();
    airplane.setAirplaneType(airplaneType);
    airplaneDao.save(airplane);
    return airplane;
  }

  public void updateAirplane(
    Long airplaneId,
    UpdateAirplaneDto updateAirplaneDto
  ) {
    Airplane airplane = airplaneDao
      .findById(airplaneId)
      .orElseThrow(AirplaneNotFoundException::new);
    updateAirplaneDto
      .getAirplaneTypeId()
      .ifPresent(
        airplaneTypeId -> {
          AirplaneType airplaneType = airplaneTypeDao
            .findById(airplaneTypeId)
            .orElseThrow(AirplaneTypeNotFoundException::new);
          airplane.setAirplaneType(airplaneType);
        }
      );
    airplaneDao.save(airplane);
  }

  public void deleteAirplane(Long airplaneId) {
    Airplane airplane = airplaneDao
      .findById(airplaneId)
      .orElseThrow(AirplaneNotFoundException::new);
    if (!airplane.getFlights().isEmpty()) {
      throw new AirplaneDeletionNotAllowedException();
    }
    airplaneDao.delete(airplane);
  }
}
