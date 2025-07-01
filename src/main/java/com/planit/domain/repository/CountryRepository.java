package com.planit.domain.repository;

import com.planit.domain.entity.Country;

import java.util.List;

public interface CountryRepository {

    void saveAll(List<Country> countries);

    List<Country> findAll();
}
