package com.planit.infrastructure.jpa.holiday;


import com.planit.application.dto.requeset.HolidaySearchRequest;
import com.planit.domain.entity.Holiday;
import com.planit.domain.model.Pagination;
import com.planit.domain.repository.HolidayRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class HolidayRepositoryImpl implements HolidayRepository {
    private final JpaHolidayRepository jpaRepository;
    private final HolidayRepositoryCustom holidayRepositoryCustom;

    @Override
    public void saveAll(List<Holiday> holidays) {
        jpaRepository.saveAll(holidays);
    }

    @Override
    public Optional<Holiday> findByCountryAndDate(String countryCode, LocalDate date) {
        return jpaRepository.findByCountryCodeAndDate(countryCode, date);
    }

    @Override
    public int deleteByCountryAndDate(String countryCode, LocalDate startDate, LocalDate endDate) {
        return jpaRepository.softDeleteByCountryCodeAndDateRange(countryCode, startDate, endDate);
    }

    @Override
    public Pagination<Holiday> searchHolidayList(HolidaySearchRequest request) {
        return holidayRepositoryCustom.searchHolidayList(request);
    }
}