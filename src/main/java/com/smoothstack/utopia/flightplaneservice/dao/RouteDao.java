package com.smoothstack.utopia.flightplaneservice.dao;

import com.smoothstack.utopia.shared.model.Airport;
import com.smoothstack.utopia.shared.model.Route;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Rob Maes
 * Mar 17 2021
 */
@Repository
public interface RouteDao extends JpaRepository<Route, Long> {
  Optional<Route> findRouteByOriginAirportAndDestinationAirport(
    Airport originAirport,
    Airport destinationAirport);
  Optional<Route[]> findAllRoutesByDestinationAirport(Airport destinationAirport);
  Optional<Route[]> findAllRoutesByOriginAirport(Airport destinationAirport);
}
