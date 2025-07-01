package com.planit.domain.entity;

import com.planit.common.entity.BaseEntity;
import com.planit.domain.id.HolidayId;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Table(name = "holiday", indexes = {@Index(name = "idx_country_date", columnList = "country_code, date")})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
@IdClass(HolidayId.class)
public class Holiday extends BaseEntity {

    @Id
    @Column(name = "country_code")
    private String countryCode;

    @Id
    private LocalDate date;

    @Column(name = "local_name", nullable = false)
    private String localName;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private boolean fixed;

    @Column(nullable = false)
    private boolean global;

    @Column(name = "launch_year")
    private Integer launchYear;

    @Column(columnDefinition = "TEXT")
    private String countries;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String types;

    @Column(nullable = false)
    private boolean status;

    public static Holiday create(
            String countryCode,
            LocalDate date,
            String name,
            String localName,
            boolean fixed,
            boolean global,
            Integer launchYear,
            String countries,
            String types
    ) {
        return Holiday.builder()
                .countryCode(countryCode)
                .date(date)
                .name(name)
                .localName(localName)
                .fixed(fixed)
                .global(global)
                .launchYear(launchYear)
                .countries(countries)
                .types(types)
                .status(true)
                .build();
    }
}
