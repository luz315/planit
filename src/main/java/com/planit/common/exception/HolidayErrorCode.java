package com.planit.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum HolidayErrorCode implements ErrorCode {

    HOLIDAY_NOT_FOUND(40000, "요청한 공휴일을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    NO_HOLIDAY_TO_DELETE(40001, "공휴일이 없습니다.", HttpStatus.BAD_REQUEST),
    JSON_SERIALIZE_FAIL(40002, "공휴일 타입 직렬화에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    JSON_PARSING_FAIL(40003, "JSON 타입 파싱에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

    private final int code;
    private final String message;
    private final HttpStatus status;
}
