package com.smoothstack.utopia.flightplaneservice.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Rob Maes
 * May 12 2021
 */
@Getter
@Setter
@ToString
public class CreateSeatLocationDto {

  private Double width;
  private Double height;
  private Character col;
  private Integer row;
}
