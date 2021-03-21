package com.smoothstack.utopia.flightplaneservice;

import com.smoothstack.utopia.shared.model.Airport;
import com.smoothstack.utopia.shared.model.Booking;
import com.smoothstack.utopia.shared.model.Route;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Rob Maes
 * Mar 21 2021
 */
@Repository
public interface BookingDao extends JpaRepository<Booking, Long> {}
