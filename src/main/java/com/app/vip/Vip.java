package com.app.vip;

import javax.persistence.*;

@Entity
@Table(name = "VIP")
public class Vip {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "REGISTRATION_PLATE", length = 12)
    private String registrationPlate;

    public Long getId() {
        return id;
    }

    public String getRegistrationPlate() {
        return registrationPlate;
    }

    public void setRegistrationPlate(String registrationPlate) {
        this.registrationPlate = registrationPlate;
    }
}
