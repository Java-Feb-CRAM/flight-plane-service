package com.smoothstack.utopia.flightplaneservice.dao;

import com.smoothstack.utopia.shared.model.Airport;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Rob Maes
 * Mar 17 2021
 */
@Repository
public interface AirportDao extends JpaRepository<Airport, String> {
	Optional<Airport> findByIataId(String idata_id);
}
