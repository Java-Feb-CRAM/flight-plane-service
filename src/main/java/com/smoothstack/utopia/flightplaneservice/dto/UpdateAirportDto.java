package com.smoothstack.utopia.flightplaneservice.dto;

import java.util.Optional;
import javax.validation.constraints.Pattern;
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
    min = 3,
    max = 3,
    message = "Airport ID must be exactly 3 characters in length"
  ) @Pattern(
    regexp = "[A-Z]{3}",
    message = "Airport ID can only contain capital letters"
  ) String> id = Optional.empty();

  private Optional<@Size(
    min = 1,
    max = 255,
    message = "Airport city must be between 1 - 255 characters"
  ) String> city = Optional.empty();
}
