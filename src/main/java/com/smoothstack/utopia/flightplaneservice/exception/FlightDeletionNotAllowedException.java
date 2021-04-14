package com.smoothstack.utopia.flightplaneservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Rob Maes
 * Mar 19 2021
 */
@ResponseStatus(
  value = HttpStatus.METHOD_NOT_ALLOWED,
  reason = "The requested flight cannot be deleted because it has bookings associated with it"
)
public class FlightDeletionNotAllowedException extends RuntimeException { private static final long serialVersionUID = 1L; }
