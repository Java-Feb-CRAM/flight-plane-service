package com.smoothstack.utopia.flightplaneservice.service;

import com.smoothstack.utopia.flightplaneservice.dao.SeatLayoutDao;
import com.smoothstack.utopia.flightplaneservice.exception.SeatLayoutNotFoundException;
import com.smoothstack.utopia.shared.model.SeatLayout;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Rob Maes
 * May 10 2021
 */
@Service
public class SeatLayoutService {

  private final SeatLayoutDao seatLayoutDao;

  @Autowired
  public SeatLayoutService(SeatLayoutDao seatLayoutDao) {
    this.seatLayoutDao = seatLayoutDao;
  }

  public List<SeatLayout> getAllSeatLayouts() {
    return seatLayoutDao.findAll();
  }

  public SeatLayout getSeatLayout(Long seatLayoutId) {
    return seatLayoutDao
      .findById(seatLayoutId)
      .orElseThrow(SeatLayoutNotFoundException::new);
  }
}
