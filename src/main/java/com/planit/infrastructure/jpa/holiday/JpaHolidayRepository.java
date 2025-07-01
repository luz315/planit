package com.planit.infrastructure.jpa.holiday;

import com.planit.domain.entity.Holiday;
import com.planit.domain.id.HolidayId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface JpaHolidayRepository extends JpaRepository<Holiday, HolidayId> {

    Optional<Holiday> findByCountryCodeAndDate(String countryCode, LocalDate date);

    @Modifying
    @Query("""
        UPDATE Holiday holiday
        SET holiday.status = true,
            holiday.deletedAt = CURRENT_TIMESTAMP
        WHERE holiday.countryCode = :countryCode
          AND holiday.date BETWEEN :start AND :end
    """)
    int softDeleteByCountryCodeAndDateRange(
            @Param("countryCode") String countryCode,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end
    );
}
