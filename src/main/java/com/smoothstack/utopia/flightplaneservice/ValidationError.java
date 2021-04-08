package com.smoothstack.utopia.flightplaneservice;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Rob Maes
 * Mar 29 2021
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ValidationError {

  private String fieldName;
  private Object rejectedValue;
  private String message;
}
