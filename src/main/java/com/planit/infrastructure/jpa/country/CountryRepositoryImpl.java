package com.planit.infrastructure.jpa.country;

import com.planit.domain.entity.Country;
import com.planit.domain.repository.CountryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CountryRepositoryImpl implements CountryRepository {

    private final JpaCountryRepository jpaCountryRepository;

    @Override
    public void saveAll(List<Country> countries) {
        jpaCountryRepository.saveAll(countries);
    }

    @Override
    public List<Country> findAll() {
        return jpaCountryRepository.findAll();
    }
}
