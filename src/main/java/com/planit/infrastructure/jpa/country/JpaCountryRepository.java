package com.planit.infrastructure.jpa.country;

import com.planit.domain.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaCountryRepository extends JpaRepository<Country, String> {

    List<Country> findAll();
}
