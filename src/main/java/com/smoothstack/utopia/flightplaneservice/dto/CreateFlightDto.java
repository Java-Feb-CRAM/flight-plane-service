package com.smoothstack.utopia.flightplaneservice.dto;

import java.time.Instant;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Rob Maes
 * Mar 18 2021
 */
@Getter
@Setter
@ToString
public class CreateFlightDto {

  @NotNull(message = "Route ID is required")
  @Positive(message = "Route ID must be a positive integer")
  private Long routeId;

  @NotNull(message = "Airplane ID is required")
  @Positive(message = "Airplane ID must be a positive integer")
  private Long airplaneId;

  @NotNull(message = "Departure time is required")
  private Instant departureTime;

  @NotNull(message = "Number of reserved seats is required")
  @PositiveOrZero(message = "Number of reserved seats must be 0 or greater")
  private Integer reservedSeats;

  @NotNull(message = "Seat price is required")
  @PositiveOrZero(message = "Seat price must be 0 or greater")
  private Float seatPrice;
}
