package com.app.vip;

import org.springframework.data.repository.Repository;
import org.springframework.data.rest.core.annotation.RestResource;
public interface VipRepository extends Repository<Vip, Long> {

    @RestResource
    Vip save(Vip vip);

    Boolean existsByRegistrationPlate(String registrationPlate);
}
