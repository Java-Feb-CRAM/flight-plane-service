package com.smoothstack.utopia.flightplaneservice.dao;

import com.smoothstack.utopia.shared.model.Flight;
import com.smoothstack.utopia.shared.model.Route;
import java.time.Instant;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Rob Maes
 * Mar 18 2021
 *
 * @editor Craig Saunders UT-60 flight search
 *
 */
@Repository
public interface FlightDao extends JpaRepository<Flight, Long> {
  Optional<Flight[]> findAllByRouteAndDepartureTime(
    Route route,
    Instant departureTime
  );

  Optional<Flight[]> findAllByRoute(Route route);

  Optional<Flight[]> findAllByRouteId(Long routeId);
}
