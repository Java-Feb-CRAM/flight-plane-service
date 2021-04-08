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
public class CreateAirportDto {

  @NotBlank(message = "Airport ID is required")
  @Size(
    min = 3,
    max = 3,
    message = "Airport ID must be exactly 3 characters in length"
  )
  @Pattern(
    regexp = "[A-Z]{3}",
    message = "Airport ID can only contain capital letters"
  )
  private String iataId;

  @NotBlank(message = "Airport city is required")
  @Size(max = 45, message = "Airport city must not exceed 45 characters")
  private String city;
}
