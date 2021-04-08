package com.smoothstack.utopia.flightplaneservice.dto;

import javax.validation.constraints.NotBlank;
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
public class CreateRouteDto {

  @NotBlank(message = "Origin airport ID is required")
  @Size(
    min = 3,
    max = 3,
    message = "Origin airport ID must be exactly 3 characters in length"
  )
  @Pattern(
    regexp = "[A-Z]{3}",
    message = "Origin airport ID can only contain capital letters"
  )
  private String originAirportId;

  @NotBlank(message = "Destination airport ID is required")
  @Size(
    min = 3,
    max = 3,
    message = "Destination airport ID must be exactly 3 characters in length"
  )
  @Pattern(
    regexp = "[A-Z]{3}",
    message = "Destination airport ID can only contain capital letters"
  )
  private String destinationAirportId;
}
