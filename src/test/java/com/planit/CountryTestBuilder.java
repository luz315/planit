package com.planit;

import com.planit.domain.entity.Country;
import org.springframework.test.util.ReflectionTestUtils;

public class CountryTestBuilder {

    private String code = "KR";
    private String name = "대한민국";

    public static CountryTestBuilder builder() {
        return new CountryTestBuilder();
    }

    public CountryTestBuilder code(String code) {
        this.code = code;
        return this;
    }

    public CountryTestBuilder name(String name) {
        this.name = name;
        return this;
    }

    public Country build() {
        Country country = Country.create(code, name);
        // BaseEntity 상속 시 createdAt, updatedAt 등을 세팅하려면 여기에 추가로 세팅 가능
        // 예) ReflectionTestUtils.setField(country, "createdAt", LocalDateTime.now());
        return country;
    }
}
