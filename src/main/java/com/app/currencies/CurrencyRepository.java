package com.app.currencies;

import org.springframework.data.repository.Repository;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.Optional;

public interface CurrencyRepository extends Repository<Currency, Long> {

    @RestResource
    Currency save(Currency currency);

    Optional<Currency> findTopByCodeEquals(String code);
}
