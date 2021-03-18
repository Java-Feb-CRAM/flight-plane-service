package com.smoothstack.utopia.flightplaneservice.service;

import com.smoothstack.utopia.flightplaneservice.dao.AirplaneDao;
import com.smoothstack.utopia.flightplaneservice.dto.CreateAirplaneDto;
import com.smoothstack.utopia.flightplaneservice.dto.UpdateAirplaneDto;
import com.smoothstack.utopia.flightplaneservice.exception.AirplaneNotFoundException;
import com.smoothstack.utopia.shared.model.Airplane;
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

  @Autowired
  public AirplaneService(AirplaneDao airplaneDao) {
    this.airplaneDao = airplaneDao;
  }

  public List<Airplane> getAllAirplanes() {
    return airplaneDao.findAll();
  }

  public Airplane getAirplane(Long airplaneId) {
    return airplaneDao
      .findById(airplaneId)
      .orElseThrow(AirplaneNotFoundException::new);
  }

  public void createAirplane(CreateAirplaneDto createAirplaneDto) {}

  public void updateAirplane(
    Long airplaneId,
    UpdateAirplaneDto updateAirplaneDto
  ) {}

  public void deleteAirplane(Long airplaneId) {}
}
