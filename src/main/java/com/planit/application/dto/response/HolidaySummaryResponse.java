package com.planit.application.dto.response;

import com.planit.domain.entity.Holiday;
import java.time.LocalDate;

public record HolidaySummaryResponse(
        String countryCode,
        LocalDate date,
        String name,
        String localName,
        boolean fixed,
        boolean global
    ) {
        public static HolidaySummaryResponse from(Holiday holiday) {
            return new HolidaySummaryResponse(
                    holiday.getCountryCode(),
                    holiday.getDate(),
                    holiday.getName(),
                    holiday.getLocalName(),
                    holiday.isFixed(),
                    holiday.isGlobal()
            );
        }
}


