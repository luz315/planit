package com.planit.application.dto.requeset;

public record HolidayRequest(
        String countryCode,
        int year
) {}