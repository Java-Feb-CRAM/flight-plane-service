package com.smoothstack.utopia.flightplaneservice.dto;

import java.util.Optional;
import javax.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Rob Maes
 * Mar 18 2021
 */
@Getter
@Setter
public class UpdateAirplaneDto {

  private Optional<@Positive(
    message = "Airplane type ID must be greater than 0"
  ) Long> airplaneTypeId = Optional.empty();
}
