package com.smoothstack.utopia.flightplaneservice.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Rob Maes
 * Mar 17 2021
 */
@Getter
@Setter
public class CreateAirplaneTypeDto {

  @NotNull(message = "Seat layout ID is required")
  @Positive(message = "Seat layout ID must be greater than 0")
  private Long seatLayoutId;
}
