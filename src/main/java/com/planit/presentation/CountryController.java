package com.planit.presentation;

import com.planit.application.CountryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/countries")
@RequiredArgsConstructor
@Tag(name = "국가 API", description = "국가 API")
public class CountryController {

    private final CountryService countryService;

    @Operation(summary = "국가 동기화", description = "전체 국가를 외부 API로부터 동기화합니다.")
    @PostMapping("/sync")
    public void syncCountryList() {
        countryService.syncCountryList();
    }
}
