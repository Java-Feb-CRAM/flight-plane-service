package com.smoothstack.utopia.flightplaneservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Rob Maes
 * Apr 27 2021
 */
@ResponseStatus(
  value = HttpStatus.BAD_REQUEST,
  reason = "A route cannot have the same origin and destination"
)
public class InvalidRouteException extends RuntimeException {}
