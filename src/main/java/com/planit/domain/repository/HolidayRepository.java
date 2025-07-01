package com.planit.domain.repository;

import com.planit.application.dto.requeset.HolidaySearchRequest;
import com.planit.domain.entity.Holiday;
import com.planit.domain.model.Pagination;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface HolidayRepository {

    void saveAll(List<Holiday> holidays);

    Optional<Holiday> findByCountryAndDate(String countryCode, LocalDate date);

    int deleteByCountryAndDate(String countryCode, LocalDate startDate, LocalDate endDate);

    Pagination<Holiday> searchHolidayList(HolidaySearchRequest holidaySearchRequest);
}

