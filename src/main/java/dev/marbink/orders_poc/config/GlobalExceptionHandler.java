package dev.marbink.orders_poc.config;

import dev.marbink.orders_poc.exception.NotFoundException;
import dev.marbink.orders_poc.exception.OutOfStockException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  @Value("${app.global-exception-handler.hide-error-details}")
  boolean hideErrorDetails;

  @Override
  protected ResponseEntity<Object> handleExceptionInternal(
      Exception ex,
      Object body,
      HttpHeaders headers,
      HttpStatusCode statusCode,
      WebRequest request) {
    if (!hideErrorDetails) {
      return super.handleExceptionInternal(ex, body, headers, statusCode, request);
    }
    // Removing body to get a uniform response across custom and default exceptions
    log.info("Exception: ", ex);
    return new ResponseEntity<>(statusCode);
  }

  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<Object> handleNotFoundException(Exception ex, HttpServletRequest request) {
    return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(OutOfStockException.class)
  public ResponseEntity<Object> handleBadRequestException(
      Exception ex, HttpServletRequest request) {
    return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
  }
}
