package com.planit.service;

import com.planit.CountryTestBuilder;
import com.planit.application.CountryService;
import com.planit.common.dto.ExternalCountryDto;
import com.planit.domain.entity.Country;
import com.planit.domain.repository.CountryRepository;
import com.planit.infrastructure.api.ExternalApiClientImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CountryServiceTest {

    @Mock private CountryRepository countryRepository;
    @Mock private ExternalApiClientImpl externalApiClient;

    @InjectMocks private CountryService countryService;

    @BeforeEach
    void setUp() {
        Mockito.reset(countryRepository, externalApiClient);
    }

    @Test
    void syncCountryList() {
        List<ExternalCountryDto> dtos = List.of(
                new ExternalCountryDto("KR", "대한민국"),
                new ExternalCountryDto("US", "미국")
        );

        when(externalApiClient.getCountryList()).thenReturn(dtos);

        countryService.syncCountryList();

        verify(countryRepository).saveAll(any());
    }

    @Test
    void getCountryList() {
        Country country = CountryTestBuilder.builder()
                .code("KR")
                .name("대한민국")
                .build();

        when(countryRepository.findAll()).thenReturn(List.of(country));

        List<Country> result = countryService.getCountryList();

        verify(countryRepository).findAll();
        assert !result.isEmpty();
    }
}
