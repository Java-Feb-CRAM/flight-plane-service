package com.smoothstack.utopia.flightplaneservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Rob Maes
 * Mar 17 2021
 */
@ResponseStatus(
  value = HttpStatus.NOT_FOUND,
  reason = "The requested airplane type does not exist"
)
public class AirplaneTypeNotFoundException extends RuntimeException {

  private static final long serialVersionUID = 1L;
}
