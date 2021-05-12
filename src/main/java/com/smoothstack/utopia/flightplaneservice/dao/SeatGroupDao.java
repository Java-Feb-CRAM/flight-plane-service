package com.smoothstack.utopia.flightplaneservice.dao;

import com.smoothstack.utopia.shared.model.SeatGroup;
import com.smoothstack.utopia.shared.model.SeatLayout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Rob Maes
 * May 12 2021
 */
@Repository
public interface SeatGroupDao extends JpaRepository<SeatGroup, Long> {}
