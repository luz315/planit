package com.planit.common.exception;

import org.springframework.http.HttpStatus;

public record ErrorResponse(
        int code,
        HttpStatus status,
        String message
) {
  public static ErrorResponse of(CustomException e) {
    return new ErrorResponse(e.getCode(), e.getStatus(), e.getMessage());
  }
}
