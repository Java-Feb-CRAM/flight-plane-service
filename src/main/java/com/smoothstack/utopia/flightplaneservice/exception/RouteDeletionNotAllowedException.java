package com.smoothstack.utopia.flightplaneservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Rob Maes
 * Mar 19 2021
 */
@ResponseStatus(
  value = HttpStatus.METHOD_NOT_ALLOWED,
  reason = "The requested route cannot be deleted because it has flights associated with it"
)
public class RouteDeletionNotAllowedException extends RuntimeException {}
