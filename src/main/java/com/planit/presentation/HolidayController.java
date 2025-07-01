package com.planit.presentation;

import com.planit.application.HolidayService;
import com.planit.application.dto.requeset.HolidayDetailRequest;
import com.planit.application.dto.requeset.HolidayRequest;
import com.planit.application.dto.requeset.HolidaySearchRequest;
import com.planit.application.dto.response.HolidayResponse;
import com.planit.application.dto.response.HolidaySummaryResponse;
import com.planit.application.dto.response.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/holidays")
@RequiredArgsConstructor
@Tag(name = "공휴일 API", description = "공휴일 API")
public class HolidayController {

    private final HolidayService holidayService;

    @Operation(summary = "전국 5년치 공휴일 동기화", description = "전체 국가의 공휴일을 최근 5년치까지 외부 API로부터 동기화합니다.")
    @PostMapping("/sync/all")
    public void syncHolidayListForFiveYears() {
        holidayService.syncHolidayListForFiveYears();
    }

    @Operation(summary = "특정 국가·연도 공휴일 동기화", description = "국가코드와 연도를 기반으로 외부 API로부터 공휴일을 동기화합니다.")
    @PostMapping("/sync")
    public void syncHolidayList(
            @RequestBody HolidayRequest request
    ) {
        holidayService.syncHolidayList(request);
    }

    @Operation(summary = "공휴일 목록 검색", description = "국가코드, 연도, 날짜 범위 등으로 공휴일 목록을 조회합니다.")
    @GetMapping
    public PageResponse<HolidaySummaryResponse> searchHolidayList(
            @ParameterObject HolidaySearchRequest request
    ) {
        return holidayService.searchHolidayList(request);
    }

    @Operation(summary = "공휴일 단건 조회", description = "국가코드와 날짜를 기준으로 단일 공휴일 정보를 조회합니다.")
    @GetMapping("/{countryCode}/{date}")
    public HolidayResponse getHoliday(
            @Parameter(description = "국가 코드", example = "KR") @PathVariable String countryCode,
            @Parameter(description = "날짜", example = "2025-10-03") @PathVariable LocalDate date
    ) {
        return holidayService.getHoliday(new HolidayDetailRequest(countryCode, date));
    }

    @Operation(summary = "특정 국가·연도 공휴일 삭제", description = "해당 국가의 특정 연도 공휴일을 소프트 삭제합니다.")
    @DeleteMapping("/{countryCode}/{year}")
    public void deleteHolidayByYear(
            @Parameter(description = "국가 코드", example = "KR") @RequestParam String countryCode,
            @Parameter(description = "연도", example = "2025") @RequestParam int year
    ) {
        holidayService.deleteHolidaysByYear(new HolidayRequest(countryCode,year));
    }
}
