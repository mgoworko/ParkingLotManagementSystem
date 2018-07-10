package com.app.registrations;

import com.app.currencies.Currency;
import com.app.currencies.CurrencyRepository;
import com.app.vip.VipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.google.common.collect.ImmutableMap;
import com.app.tariffs.Tariff;
import com.app.tariffs.TariffRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@RestController
public class RegistrationController {

    private RegistrationRepository registrationRepository;

    private TariffRepository tariffRepository;

    private VipRepository vipRepository;

    private CurrencyRepository currencyRepository;

    @Autowired
    public RegistrationController(  RegistrationRepository registrationRepository,
                                    TariffRepository tariffRepository,
                                    VipRepository vipRepository,
                                    CurrencyRepository currencyRepository) {
        this.registrationRepository = registrationRepository;
        this.tariffRepository = tariffRepository;
        this.vipRepository = vipRepository;
        this.currencyRepository = currencyRepository;
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/Warsaw"));
    }

    @PostMapping("/register")
    public ResponseEntity<Void> registerCar(@RequestBody RegistrationRequest pRegistration) {
        if(registrationRepository
                .findTopByRegistrationPlateAndDepartureIsNullOrderByArrivalDesc(pRegistration.getRegistrationPlate())
                .isPresent()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if(pRegistration
                .getRegistrationPlate()
                .length() > 12) {
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
        registrationRepository.save(createRegistration(pRegistration.getRegistrationPlate()));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/checkout")
    public ResponseEntity<ImmutableMap<String, String>> carFeeLookup(@RequestBody RegistrationRequest pRegistration) {
        Optional<Registration> registrationOptional = registrationRepository
                .findTopByRegistrationPlateAndDepartureIsNullOrderByArrivalDesc(pRegistration.getRegistrationPlate());
        if(registrationOptional.isPresent()) {
            Registration registration = registrationOptional.get();
            Optional<Tariff> tariff = tariffRepository.findOne(registration.getTariffId());
            if(tariff.isPresent()) {
                LocalDateTime now = LocalDateTime.now();
                BigDecimal fee = calculatePrice(registration, tariff.get(), now, pRegistration.getCurrency());
                return new ResponseEntity<>(ImmutableMap.of("fee", fee.toString()
                        , "registrationPlate", registration.getRegistrationPlate()
                        , "arrival", registration.getArrival().toString()
                        , "departure", now.toString()
                        , "currency", pRegistration.getCurrency()),HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/unregister")
    public ResponseEntity<Void> unregisterCar(@RequestBody Registration pRegistration) {
        Optional<Registration> registrationOptional = registrationRepository
                .findTopByRegistrationPlateOrderByArrivalDesc(pRegistration.getRegistrationPlate());
        if(registrationOptional.isPresent()) {
            Registration registration = registrationOptional.get();
            registration.setDeparture(pRegistration.getDeparture());
            registrationRepository.save(registration);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    private Registration createRegistration(String registrationPlate) {
        Registration registration = new Registration();
        registration.setRegistrationPlate(registrationPlate);
        registration.setArrival(LocalDateTime.now());
        registration.setDeparture((LocalDateTime)null);
        registration.setTariffId(tariffRepository.findTopByVipEqualsOrderByIdDesc(vipRepository.existsByRegistrationPlate(registrationPlate)).getId());
        return registration;
    }

    private BigDecimal calculatePrice(Registration registration, Tariff tariff, LocalDateTime now, String currency) {
        long hours = registration.getArrival().until(now, ChronoUnit.HOURS);
        long minutes = registration.getArrival().until(now, ChronoUnit.MINUTES) % 60;
        long seconds = registration.getArrival().until(now, ChronoUnit.SECONDS) % 60;
        if(minutes>0 || seconds>0) hours+=1;

        BigDecimal fee=new BigDecimal(0);

        if(hours>=1) fee = fee.add(tariff.getFirst());
        if(hours>=2) fee = fee.add(tariff.getSecond());

        BigDecimal feePerHour=new BigDecimal(tariff.getSecond().doubleValue()*tariff.getNext());
        for (int i=0;i<hours-2;i++){
            fee = fee.add(feePerHour);
            feePerHour=new BigDecimal(feePerHour.doubleValue()*tariff.getNext());
        }
        Optional<Currency> c = currencyRepository.findTopByCodeEquals(currency);
        if(!c.isPresent()) return fee.setScale(2, BigDecimal.ROUND_HALF_UP);
        fee=fee.multiply(new BigDecimal(c.get().getRate()));
        return fee.setScale(2, BigDecimal.ROUND_HALF_UP);
    }
}