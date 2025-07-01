package com.planit;

import com.planit.domain.entity.Country;
import com.planit.domain.entity.Holiday;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;

public class HolidayTestBuilder {

    private String countryCode = "KR";
    private LocalDate date = LocalDate.of(2025, 9, 8);
    private String name = "추석";
    private String localName = "Chuseok";
    private boolean fixed = false;
    private boolean global = false;
    private Integer launchYear = 1945;
    private String countries = "[\"KR\"]";
    private String types = "[\"PUBLIC\"]";

    public static HolidayTestBuilder builder() {
        return new HolidayTestBuilder();
    }

    public HolidayTestBuilder countryCode(String countryCode) {
        this.countryCode = countryCode;
        return this;
    }

    public HolidayTestBuilder date(LocalDate date) {
        this.date = date;
        return this;
    }

    public HolidayTestBuilder name(String name) {
        this.name = name;
        return this;
    }

    public HolidayTestBuilder localName(String localName) {
        this.localName = localName;
        return this;
    }

    public HolidayTestBuilder countries(String countriesJson) {
        this.countries = countriesJson;
        return this;
    }

    public HolidayTestBuilder types(String typesJson) {
        this.types = typesJson;
        return this;
    }

    public Holiday build() {
        Holiday holiday = Holiday.create(
                countryCode,
                date,
                name,
                localName,
                fixed,
                global,
                launchYear,
                countries,
                types
        );
        // 필요 시 추가 필드 세팅
        return holiday;
    }
}
