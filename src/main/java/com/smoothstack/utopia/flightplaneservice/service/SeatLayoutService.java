package com.smoothstack.utopia.flightplaneservice.service;

import com.smoothstack.utopia.flightplaneservice.dao.SeatGroupDao;
import com.smoothstack.utopia.flightplaneservice.dao.SeatLayoutDao;
import com.smoothstack.utopia.flightplaneservice.dao.SeatLocationDao;
import com.smoothstack.utopia.flightplaneservice.dto.CreateSeatLayoutDto;
import com.smoothstack.utopia.flightplaneservice.exception.SeatLayoutNotFoundException;
import com.smoothstack.utopia.shared.model.SeatGroup;
import com.smoothstack.utopia.shared.model.SeatLayout;
import com.smoothstack.utopia.shared.model.SeatLocation;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Rob Maes
 * May 10 2021
 */
@Service
public class SeatLayoutService {

  private final SeatLayoutDao seatLayoutDao;
  private final SeatGroupDao seatGroupDao;
  private final SeatLocationDao seatLocationDao;

  @Autowired
  public SeatLayoutService(
    SeatLayoutDao seatLayoutDao,
    SeatGroupDao seatGroupDao,
    SeatLocationDao seatLocationDao
  ) {
    this.seatLayoutDao = seatLayoutDao;
    this.seatGroupDao = seatGroupDao;
    this.seatLocationDao = seatLocationDao;
  }

  public List<SeatLayout> getAllSeatLayouts() {
    return seatLayoutDao.findAll();
  }

  public SeatLayout getSeatLayout(Long seatLayoutId) {
    return seatLayoutDao
      .findById(seatLayoutId)
      .orElseThrow(SeatLayoutNotFoundException::new);
  }

  @Transactional
  public SeatLayout createSeatLayout(CreateSeatLayoutDto createSeatLayoutDto) {
    System.out.println(createSeatLayoutDto);
    SeatLayout seatLayout = new SeatLayout();
    seatLayoutDao.save(seatLayout);
    //    List<SeatGroup> seatGroups = new ArrayList<>();
    createSeatLayoutDto
      .getSeatGroups()
      .forEach(
        group -> {
          //          List<SeatLocation> seatLocations = new ArrayList<>();
          SeatGroup seatGroup = new SeatGroup();
          char[] cols = new char[group.getColumns().size()];
          for (int i = 0; i < group.getColumns().size(); i++) {
            cols[i] = group.getColumns().get(i);
          }
          seatGroup.setColumns(cols);
          seatGroup.setName(group.getName());
          seatGroup.setSeatLayout(seatLayout);
          seatGroupDao.save(seatGroup);
          //          seatGroup.setSeatLocations(new HashSet<>(seatLocations));
          //          seatGroups.add(seatGroup);
          group
            .getSeatLocations()
            .forEach(
              seat -> {
                SeatLocation seatLocation = new SeatLocation();
                seatLocation.setCol(seat.getCol());
                seatLocation.setHeight(seat.getHeight());
                seatLocation.setRow(seat.getRow());
                seatLocation.setWidth(seat.getWidth());
                seatLocation.setSeatGroup(seatGroup);
                seatLocationDao.save(seatLocation);
              }
            );
        }
      );

    return seatLayout;
  }
}
