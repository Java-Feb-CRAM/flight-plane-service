package com.smoothstack.utopia.flightplaneservice.dto;

import java.util.Optional;
import javax.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Rob Maes
 * Mar 17 2021
 */
@Getter
@Setter
public class UpdateAirplaneTypeDto {

  private Optional<@Positive(
    message = "Max capacity must be greater than 0"
  ) Integer> maxCapacity = Optional.empty();
}
