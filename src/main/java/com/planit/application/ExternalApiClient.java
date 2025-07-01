package com.planit.application;

import com.planit.common.dto.ExternalCountryDto;
import com.planit.common.dto.ExternalHolidayDto;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface ExternalApiClient {

    public List<ExternalCountryDto> getCountryList();

    public CompletableFuture<List<ExternalHolidayDto>> getHolidayList(String countryCode, int year);
}
