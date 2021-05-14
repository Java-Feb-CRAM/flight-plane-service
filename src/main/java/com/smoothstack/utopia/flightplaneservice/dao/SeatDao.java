package com.smoothstack.utopia.flightplaneservice.dao;

import com.smoothstack.utopia.shared.model.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Rob Maes
 * May 14 2021
 */
@Repository
public interface SeatDao extends JpaRepository<Seat, Long> {}
