package com.planit.infrastructure.api;

import com.planit.application.ExternalApiClient;
import com.planit.common.dto.ExternalCountryDto;
import com.planit.common.dto.ExternalHolidayDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExternalApiClientImpl implements ExternalApiClient {

    private final WebClient webClient;

    // 국가 api
    @Override
    public List<ExternalCountryDto> getCountryList() {
        ExternalCountryDto[] response = webClient.get()
                .uri("/AvailableCountries")
                .retrieve()
                .bodyToMono(ExternalCountryDto[].class)
                .block();

        if (response == null) {
            log.warn("국가 리스트 API 응답이 null입니다.");
            return List.of();
        }

        return List.of(response);
    }

    // 공휴일 api
    @Override
    public CompletableFuture<List<ExternalHolidayDto>> getHolidayList(String countryCode, int year) {
        return webClient.get()
                .uri("/PublicHolidays/{year}/{countryCode}", year, countryCode)
                .retrieve()
                .bodyToMono(ExternalHolidayDto[].class)
                .map(List::of)
                .onErrorReturn(List.of())
                .toFuture();
    }
}
