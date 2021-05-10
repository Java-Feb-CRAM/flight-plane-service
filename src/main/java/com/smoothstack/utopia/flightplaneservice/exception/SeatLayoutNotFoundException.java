package com.smoothstack.utopia.flightplaneservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Rob Maes
 * May 10 2021
 */
@ResponseStatus(
  value = HttpStatus.NOT_FOUND,
  reason = "The requested seat layout does not exist"
)
public class SeatLayoutNotFoundException extends RuntimeException {

  private static final long serialVersionUID = 1L;
}
