package com.smoothstack.utopia.flightplaneservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Rob Maes
 * Mar 17 2021
 */
@ResponseStatus(
  value = HttpStatus.CONFLICT,
  reason = "An airport with this ID already exists"
)
public class DuplicateAirportException extends RuntimeException { private static final long serialVersionUID = 1L; }
