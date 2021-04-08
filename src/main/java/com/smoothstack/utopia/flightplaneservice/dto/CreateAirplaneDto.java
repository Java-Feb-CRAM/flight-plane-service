package com.smoothstack.utopia.flightplaneservice.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Rob Maes
 * Mar 18 2021
 */
@Getter
@Setter
public class CreateAirplaneDto {

  @NotNull(message = "Airplane type ID is required")
  @Positive(message = "Airplane type ID must be greater than 0")
  private Long airplaneTypeId;
}
