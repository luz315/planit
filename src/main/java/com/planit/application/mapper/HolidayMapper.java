package com.planit.application.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.planit.common.dto.ExternalHolidayDto;
import com.planit.common.exception.CustomException;
import com.planit.common.exception.HolidayErrorCode;
import com.planit.domain.entity.Holiday;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HolidayMapper {

    private final ObjectMapper objectMapper;

    public Holiday toEntity(String countryCode, ExternalHolidayDto dto) {
        return Holiday.create(
                countryCode,
                dto.date(),
                dto.name(),
                dto.localName(),
                dto.fixed(),
                dto.global(),
                dto.launchYear(),
                serializeTypes(dto.countries()),
                serializeTypes(dto.types())
        );
    }

    private String serializeTypes(java.util.List<String> types) {
        try {
            return objectMapper.writeValueAsString(types);
        } catch (JsonProcessingException e) {
            throw new CustomException(HolidayErrorCode.JSON_SERIALIZE_FAIL);
        }
    }
}
