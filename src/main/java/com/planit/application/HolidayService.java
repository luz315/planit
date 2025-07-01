package com.planit.application;

import com.planit.application.dto.requeset.HolidayDetailRequest;
import com.planit.application.dto.requeset.HolidayRequest;
import com.planit.application.dto.requeset.HolidaySearchRequest;
import com.planit.application.dto.response.HolidaySummaryResponse;
import com.planit.application.dto.response.HolidayResponse;
import com.planit.application.dto.response.PageResponse;
import com.planit.application.mapper.HolidayMapper;
import com.planit.common.exception.CustomException;
import com.planit.common.exception.HolidayErrorCode;
import com.planit.domain.entity.Country;
import com.planit.domain.entity.Holiday;
import com.planit.domain.model.Pagination;
import com.planit.domain.repository.HolidayRepository;
import com.planit.infrastructure.api.ExternalApiClientImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class HolidayService {

    private final HolidayRepository holidayRepository;
    private final ExternalApiClientImpl externalApiClient;
    private final CountryService countryService;
    private final HolidayMapper holidayMapper;

    // 5년치 공휴일 적재
    @Transactional
    public void syncHolidayListForFiveYears() {
        int current = LocalDate.now().getYear();
        int start = current - 5;

        List<String> countryCodes = getAllCountries();

        List<CompletableFuture<Void>> tasks = new ArrayList<>();

        for (String countryCode : countryCodes) {
            for (int year = start; year <= current; year++) {
                tasks.add(getAllHolidays(countryCode, year));
            }
        }

        CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0]))
                .thenRun(() -> log.info("공휴일 동기화 완료"))
                .join();

    }

    // 특정 연도·국가의 공휴일 동기화
    @Transactional
    public void syncHolidayList(HolidayRequest request) {
        fetchAndSaveHolidays(request.countryCode(), request.year()).join();
    }

    // 공휴일 검색
    @Transactional(readOnly = true)
    public PageResponse<HolidaySummaryResponse> searchHolidayList(HolidaySearchRequest request) {
        Pagination<Holiday> pagination = holidayRepository.searchHolidayList(request);
        return PageResponse.from(pagination).map(HolidaySummaryResponse::from);
    }

    // 공휴일 단건 조회
    @Transactional(readOnly = true)
    public HolidayResponse getHoliday(HolidayDetailRequest request) {
        Holiday holiday = holidayRepository.findByCountryAndDate(request.countryCode(), request.date())
                .orElseThrow(() ->
                        new CustomException(HolidayErrorCode.HOLIDAY_NOT_FOUND)
                );
        return HolidayResponse.from(holiday);
    }

    // 특정 연도·국가의 공휴일 전체 삭제
    @Transactional
    public void deleteHolidaysByYear(HolidayRequest request) {
        LocalDate start = LocalDate.of(request.year(), 1, 1);
        LocalDate end = LocalDate.of(request.year(), 12, 31);

        int deletedCount = holidayRepository.deleteByCountryAndDate(request.countryCode(), start, end);
        if (deletedCount==0) {
            throw new CustomException(HolidayErrorCode.NO_HOLIDAY_TO_DELETE);
        }
        log.info("[{}-{}] 공휴일 {}건 soft delete 완료", request.countryCode(), request.year(), deletedCount);
    }

    // 스케줄러용 메서드
    @Transactional
    public void syncSingleCountryHoliday(String countryCode, int year) {
        fetchAndSaveHolidays(countryCode, year).join();
    }

    private CompletableFuture<Void> fetchAndSaveHolidays(String countryCode, int year) {
        return externalApiClient.getHolidayList(countryCode, year)
                .exceptionally(ex -> {
                    log.error("[{}-{}] 공휴일 API 호출 실패: {}", countryCode, year, ex.getMessage(), ex);
                    return List.of();
                })
                .thenAccept(dtoList -> {
                    if (!dtoList.isEmpty()) {
                        List<Holiday> holidays = dtoList.stream()
                                .map(dto -> holidayMapper.toEntity(countryCode, dto))
                                .toList();

                        try {
                            holidayRepository.saveAll(holidays);
                        } catch (Exception e) {
                            log.error("[{}-{}] 공휴일 저장 실패", countryCode, year, e);
                        }
                        log.info("[{}-{}] 공휴일 {}건 저장 완료", countryCode, year, holidays.size());
                    }
                });
    }

    private List<String> getAllCountries() {
        return countryService.getCountryList().stream()
                .map(Country::getCode)
                .toList();
    }

    private CompletableFuture<Void> getAllHolidays(String countryCode, int year) {
        return CompletableFuture.runAsync(() -> {
            try {
                fetchAndSaveHolidays(countryCode, year).join();
            } catch (Exception e) {
                log.error("[{}-{}] 공휴일 동기화 실패: {}", countryCode, year, e.getMessage(), e);
            }
        });
    }
}
