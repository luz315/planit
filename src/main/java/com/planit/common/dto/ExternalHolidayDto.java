package com.planit.common.dto;

import java.time.LocalDate;
import java.util.List;

public record ExternalHolidayDto(
    LocalDate date,
    String localName,
    String name,
    String countryCode,
    boolean fixed,
    boolean global,
    List<String> countries,
    Integer launchYear,
    List<String> types
) {}
