package com.planit.scheduler;

import com.planit.CountryTestBuilder;
import com.planit.application.CountryService;
import com.planit.application.HolidayService;
import com.planit.domain.entity.Country;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.concurrent.Executor;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HolidaySyncSchedulerTest {

    @Mock private HolidayService holidayService;

    @Mock private CountryService countryService;

    @InjectMocks private HolidaySyncScheduler scheduler;

    @Mock private Executor holidayExecutor;

    @Test
    void syncAllCountriesForCurrentYear() {
        // given
        Country kr = CountryTestBuilder.builder()
                .code("KR")
                .name("Korea")
                .build();

        Country us = CountryTestBuilder.builder()
                .code("US")
                .name("USA")
                .build();

        when(countryService.getCountryList()).thenReturn(List.of(kr, us));

        doAnswer(invocation -> {
            Runnable task = invocation.getArgument(0);
            task.run();
            return null;
        }).when(holidayExecutor).execute(any());

        // when
        scheduler.syncAllCountriesForCurrentYear();

        // then
        verify(countryService).getCountryList();
        verify(holidayService, atLeastOnce()).syncSingleCountryHoliday(anyString(), anyInt());
    }
}
