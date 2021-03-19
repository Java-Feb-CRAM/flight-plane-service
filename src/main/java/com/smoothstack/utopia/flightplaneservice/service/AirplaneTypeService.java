package com.smoothstack.utopia.flightplaneservice.service;

import com.smoothstack.utopia.flightplaneservice.dao.AirplaneTypeDao;
import com.smoothstack.utopia.flightplaneservice.dto.CreateAirplaneTypeDto;
import com.smoothstack.utopia.flightplaneservice.dto.UpdateAirplaneTypeDto;
import com.smoothstack.utopia.flightplaneservice.exception.AirplaneTypeDeletionNotAllowedException;
import com.smoothstack.utopia.flightplaneservice.exception.AirplaneTypeNotFoundException;
import com.smoothstack.utopia.shared.model.AirplaneType;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Rob Maes
 * Mar 17 2021
 */
@Service
public class AirplaneTypeService {

  private final AirplaneTypeDao airplaneTypeDao;

  @Autowired
  public AirplaneTypeService(AirplaneTypeDao airplaneTypeDao) {
    this.airplaneTypeDao = airplaneTypeDao;
  }

  public List<AirplaneType> getAllAirplaneTypes() {
    return airplaneTypeDao.findAll();
  }

  public AirplaneType getAirplaneType(Long airplaneTypeId) {
    return airplaneTypeDao
      .findById(airplaneTypeId)
      .orElseThrow(AirplaneTypeNotFoundException::new);
  }

  public AirplaneType createAirplaneType(
    CreateAirplaneTypeDto createAirplaneTypeDto
  ) {
    AirplaneType airplaneType = new AirplaneType();
    airplaneType.setMaxCapacity(createAirplaneTypeDto.getMaxCapacity());
    airplaneTypeDao.save(airplaneType);
    return airplaneType;
  }

  public void updateAirplaneType(
    Long airplaneTypeId,
    UpdateAirplaneTypeDto updateAirplaneTypeDto
  ) {
    AirplaneType airplaneType = airplaneTypeDao
      .findById(airplaneTypeId)
      .orElseThrow(AirplaneTypeNotFoundException::new);
    updateAirplaneTypeDto
      .getMaxCapacity()
      .ifPresent(airplaneType::setMaxCapacity);
    airplaneTypeDao.save(airplaneType);
  }

  public void deleteAirplaneType(Long airplaneTypeId) {
    AirplaneType airplaneType = airplaneTypeDao
      .findById(airplaneTypeId)
      .orElseThrow(AirplaneTypeNotFoundException::new);
    if (!airplaneType.getAirplanes().isEmpty()) {
      throw new AirplaneTypeDeletionNotAllowedException();
    }
    airplaneTypeDao.delete(airplaneType);
  }
}
