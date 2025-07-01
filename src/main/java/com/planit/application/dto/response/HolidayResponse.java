package com.planit.application.dto.response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.planit.common.exception.CustomException;
import com.planit.common.exception.HolidayErrorCode;
import com.planit.domain.entity.Holiday;

import java.time.LocalDate;
import java.util.List;

public record HolidayResponse(
        String countryCode,
        LocalDate date,
        String name,
        String localName,
        boolean fixed,
        boolean global,
        Integer launchYear,
        List<String> countries,
        List<String> types
) {
    public static HolidayResponse from(Holiday holiday) {
        return new HolidayResponse(
                holiday.getCountryCode(),
                holiday.getDate(),
                holiday.getName(),
                holiday.getLocalName(),
                holiday.isFixed(),
                holiday.isGlobal(),
                holiday.getLaunchYear(),
                parseJson(holiday.getCountries()),
                parseJson(holiday.getTypes())
        );
    }

    private static List<String> parseJson(String json) {
        if (json == null || json.isBlank()) {
            return List.of();
        }
        try {
            return new ObjectMapper().readValue(json, new TypeReference<List<String>>() {});
        } catch (JsonProcessingException e) {
            throw new CustomException(HolidayErrorCode.JSON_PARSING_FAIL);
        }
    }

}
