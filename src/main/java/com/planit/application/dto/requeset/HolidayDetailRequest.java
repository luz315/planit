package com.planit.application.dto.requeset;

import java.time.LocalDate;

public record HolidayDetailRequest(
    String countryCode,
    LocalDate date
) {}
