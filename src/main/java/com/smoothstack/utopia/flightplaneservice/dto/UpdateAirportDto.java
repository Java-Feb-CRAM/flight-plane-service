package com.smoothstack.utopia.flightplaneservice.dto;

import java.util.Optional;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Rob Maes
 * Mar 17 2021
 */
@Getter
@Setter
public class UpdateAirportDto {

  private Optional<@Size(
    min = 1,
    max = 45,
    message = "Airport city must be between 1 - 45 characters"
  ) String> city = Optional.empty();
}
