package com.planit.application.dto.requeset;

import java.time.LocalDate;

public record HolidaySearchRequest(
        String countryCode,
        LocalDate from,
        LocalDate to,
        String types,
        String keyword,
        String sort,
        int page,
        int size
) {}
