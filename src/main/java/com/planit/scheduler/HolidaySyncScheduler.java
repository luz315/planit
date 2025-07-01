package com.planit.scheduler;

import com.planit.application.CountryService;
import com.planit.application.HolidayService;
import com.planit.domain.entity.Country;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Slf4j
@Component
@RequiredArgsConstructor
public class HolidaySyncScheduler {

    private final HolidayService holidayService;
    private final CountryService countryService;
    private final Executor holidayExecutor;

    @Scheduled(cron = "0 0 1 2 1 *", zone = "Asia/Seoul")
    public void syncAllCountriesForCurrentYear() {
        int current = LocalDate.now().getYear();
        int previous = current - 1;
        List<String> countries = countryService.getCountryList().stream()
                .map(Country::getCode)
                .toList();

        List<CompletableFuture<Void>> tasks = new ArrayList<>();

        for (String countryCode : countries) {
            for (int year : List.of(previous, current)) {
                tasks.add(CompletableFuture.runAsync(() -> {
                    try {
                        holidayService.syncSingleCountryHoliday(countryCode, year);
                    } catch (Exception e) {
                        log.error("[{}-{}] 공휴일 동기화 실패", countryCode, year, e);
                    }
                }, holidayExecutor));
            }
        }

        CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0]))
                .thenRun(() -> log.info("전체 공휴일 동기화 완료"))
                .join();
    }

}