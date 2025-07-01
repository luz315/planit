package com.planit.service;

import com.planit.HolidayTestBuilder;
import com.planit.application.CountryService;
import com.planit.application.HolidayService;
import com.planit.application.dto.requeset.HolidayDetailRequest;
import com.planit.application.dto.requeset.HolidayRequest;
import com.planit.application.dto.requeset.HolidaySearchRequest;
import com.planit.application.dto.response.PageResponse;
import com.planit.application.mapper.HolidayMapper;
import com.planit.common.dto.ExternalHolidayDto;
import com.planit.domain.entity.Country;
import com.planit.domain.entity.Holiday;
import com.planit.domain.model.Pagination;
import com.planit.domain.repository.HolidayRepository;
import com.planit.infrastructure.api.ExternalApiClientImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HolidayServiceTest {

    @Mock
    private HolidayRepository holidayRepository;

    @Mock
    private ExternalApiClientImpl externalApiClient;

    @Mock
    private CountryService countryService;

    @InjectMocks
    private HolidayService holidayService;

    @Mock
    private HolidayMapper holidayMapper;

    private final String countryCode = "KR";
    private final int year = 2024;

    @Test
    void syncHolidayList() {
        // given
        HolidayRequest request = new HolidayRequest(countryCode, year);
        ExternalHolidayDto dto = new ExternalHolidayDto(
                LocalDate.of(2024, 2, 10),
                "설날",
                "Seollal",
                "KR",
                true,
                false,
                List.of("KR"),
                1980,
                List.of("PUBLIC")
        );

        when(externalApiClient.getHolidayList(countryCode, year))
                .thenReturn(CompletableFuture.completedFuture(List.of(dto)));

        // when
        holidayService.syncHolidayList(request);

        // then
        verify(holidayRepository).saveAll(any());
    }

    @Test
    void syncHolidayListForFiveYears() {
        // given
        int currentYear = LocalDate.now().getYear();
        List<String> countryCodes = List.of(countryCode);
        ExternalHolidayDto dto = new ExternalHolidayDto(
                LocalDate.of(2024, 2, 10),
                "설날",
                "Seollal",
                "KR",
                true,
                false,
                List.of("KR"),
                1980,
                List.of("PUBLIC")
        );

        when(countryService.getCountryList())
                .thenReturn(List.of(Country.create(countryCode, "대한민국")));

        when(externalApiClient.getHolidayList(eq(countryCode), anyInt()))
                .thenReturn(CompletableFuture.completedFuture(List.of(dto)));

        // when
        holidayService.syncHolidayListForFiveYears();

        // then
        verify(holidayRepository, atLeast(1)).saveAll(any());
    }

    @Test
    void searchHolidayList() {
        // given
        Holiday holiday = Holiday.create(
                countryCode,
                LocalDate.of(2025, 1, 1),
                "신정",
                "New Year's Day",
                true,
                true,
                1948,
                "KR",
                "PUBLIC"
        );

        HolidaySearchRequest request = new HolidaySearchRequest(
                countryCode,
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 12, 31), null,
                null,
                null,
                0,
                10);
        Pagination<Holiday> pagination = Pagination.of(
                List.of(holiday), 0, 10, 1
        );

        when(holidayRepository.searchHolidayList(request)).thenReturn(pagination);

        // when
        PageResponse<?> response = holidayService.searchHolidayList(request);

        // then
        assertThat(response.totalElements()).isEqualTo(1);
        assertThat(response.contents().get(0)).extracting("name").isEqualTo("신정");
    }

    @Test
    void getHoliday() {
        // given
        Holiday holiday = HolidayTestBuilder.builder()
                .countryCode("KR")
                .date(LocalDate.of(2024, 1, 1))
                .types("[\"PUBLIC\"]")
                .countries("[\"KR\"]")
                .build();

        when(holidayRepository.findByCountryAndDate("KR", LocalDate.of(2024, 1, 1)))
                .thenReturn(Optional.of(holiday));

        HolidayDetailRequest request = new HolidayDetailRequest("KR", LocalDate.of(2024, 1, 1));

        // when
        var response = holidayService.getHoliday(request);

        // then
        assertThat(response.name()).isEqualTo(holiday.getName());
    }


    @Test
    void deleteHolidaysByYear() {
        // given
        HolidayRequest request = new HolidayRequest("KR", 2024);
        when(holidayRepository.deleteByCountryAndDate(any(), any(), any())).thenReturn(10);

        // when
        holidayService.deleteHolidaysByYear(request);

        // then
        verify(holidayRepository).deleteByCountryAndDate(eq("KR"),
                eq(LocalDate.of(2024, 1, 1)), eq(LocalDate.of(2024, 12, 31)));
    }

    @Test
    void syncSingleCountryHoliday() {
        // given
        String code = "KR";
        int year = 2025;
        ExternalHolidayDto dto = new ExternalHolidayDto(
                LocalDate.of(2024, 2, 10),
                "설날",
                "Seollal",
                "KR",
                true,
                false,
                List.of("KR"),
                1980,
                List.of("PUBLIC")
        );

        when(externalApiClient.getHolidayList(code, year))
                .thenReturn(CompletableFuture.completedFuture(List.of(dto)));

        // when
        holidayService.syncSingleCountryHoliday(code, year);

        // then
        verify(holidayRepository).saveAll(any());
    }
}
