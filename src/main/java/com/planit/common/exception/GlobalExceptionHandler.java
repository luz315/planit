package com.planit.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(CustomException.class)
  public ResponseEntity<ErrorResponse> handleCustomException(CustomException e, HttpServletRequest request) {
    log.error("[{}] {} - {}", request.getRequestURI(), e.getStatus(), e.getMessage());
    return ResponseEntity.status(e.getStatus())
            .body(ErrorResponse.of(e));
  }
}
