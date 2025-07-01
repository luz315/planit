package com.planit.application;

import com.planit.domain.entity.Country;
import com.planit.domain.repository.CountryRepository;
import com.planit.infrastructure.api.ExternalApiClientImpl;
import com.planit.common.dto.ExternalCountryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CountryService {

    private final CountryRepository countryRepository;
    private final ExternalApiClientImpl externalApiClient;

    // 국가 리스트 조회
    @Transactional(readOnly = true)
    public List<Country> getCountryList() {
        return countryRepository.findAll();
    }

    // 국가 동기화
    @Transactional
    public void syncCountryList() {
        List<ExternalCountryDto> countryDtos = externalApiClient.getCountryList();
        List<Country> countries = countryDtos.stream()
                .map(dto -> Country.create(dto.countryCode(), dto.name()))
                .toList();
        countryRepository.saveAll(countries);
    }
}
