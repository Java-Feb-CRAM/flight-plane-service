package com.smoothstack.utopia.flightplaneservice.dto;

import java.time.Instant;
import java.util.Optional;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Rob Maes
 * Mar 18 2021
 */
@Getter
@Setter
public class UpdateFlightDto {

  private Optional<@Positive(
    message = "Route ID must be a positive integer"
  ) Long> routeId = Optional.empty();

  private Optional<@Positive(
    message = "Airplane ID must be a positive integer"
  ) Long> airplaneId = Optional.empty();

  private Optional<Instant> departureTime = Optional.empty();

  private Optional<@PositiveOrZero(
    message = "Number of reserved seats must be 0 or greater"
  ) Integer> reservedSeats = Optional.empty();

  private Optional<@PositiveOrZero(
    message = "Seat price must be 0 or greater"
  ) Float> seatPrice = Optional.empty();
}
