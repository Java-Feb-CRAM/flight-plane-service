package com.smoothstack.utopia.flightplaneservice.service;

import com.smoothstack.utopia.flightplaneservice.dao.AirplaneTypeDao;
import com.smoothstack.utopia.flightplaneservice.dto.CreateAirplaneTypeDto;
import com.smoothstack.utopia.flightplaneservice.dto.UpdateAirplaneTypeDto;
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

  public void createAirplaneType(CreateAirplaneTypeDto createAirplaneTypeDto) {
    //TODO: implement createAirplaneType
  }

  public void updateAirplaneType(
    Long airplaneTypeId,
    UpdateAirplaneTypeDto updateAirplaneTypeDto
  ) {
    //TODO: implement updateAirplaneType
  }

  public void deleteAirplaneType(Long airplaneTypeId) {
    //TODO: implement deleteAirplaneType
  }
}
