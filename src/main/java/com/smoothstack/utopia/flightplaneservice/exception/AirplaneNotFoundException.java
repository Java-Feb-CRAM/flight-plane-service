package com.smoothstack.utopia.flightplaneservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Rob Maes
 * Mar 18 2021
 */
@ResponseStatus(
  value = HttpStatus.NOT_FOUND,
  reason = "The requested airplane does not exist"
)
public class AirplaneNotFoundException extends RuntimeException {

  private static final long serialVersionUID = 1L;
}
