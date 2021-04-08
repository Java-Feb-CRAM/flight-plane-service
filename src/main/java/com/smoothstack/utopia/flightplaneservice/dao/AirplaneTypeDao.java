package com.smoothstack.utopia.flightplaneservice.dao;

import com.smoothstack.utopia.shared.model.AirplaneType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Rob Maes
 * Mar 17 2021
 */
@Repository
public interface AirplaneTypeDao extends JpaRepository<AirplaneType, Long> {}
