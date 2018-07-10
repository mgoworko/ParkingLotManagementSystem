package com.app.tariffs;

import org.springframework.data.repository.Repository;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.Optional;

public interface TariffRepository extends Repository<Tariff, Long> {

    @RestResource
    Tariff save(Tariff tariff);

    Optional<Tariff> findOne(Long id);

    Tariff findTopByVipEqualsOrderByIdDesc(Boolean vip);
}
