package com.app.currencies;

import javax.persistence.*;

@Entity
@Table(name = "CURRENCIES")
public class Currency {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "CODE", length = 8)
    private String code;

    @Column(name = "RATE")
    private Double rate;

    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }
}
