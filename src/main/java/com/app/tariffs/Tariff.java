package com.app.tariffs;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "TARIFFS")
public class Tariff {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "FIRST", precision = 8, scale = 2)
    private BigDecimal first;

    @Column(name = "SECOND")
    private BigDecimal second;

    @Column(name = "NEXT", precision = 8, scale = 2)
    private Double next;

    @Column(name = "VIP", precision = 8, scale = 2)
    private Boolean vip;

    public Long getId() {
        return id;
    }

    public BigDecimal getFirst() {
        return first;
    }

    public void setFirst(BigDecimal first) {
        this.first = first;
    }

    public BigDecimal getSecond() {
        return second;
    }

    public void setSecond(BigDecimal second) {
        this.second = second;
    }

    public Double getNext() {
        return next;
    }

    public void setNext(Double next) {
        this.next = next;
    }

    public Boolean getVip() { return vip; }

    public void setVip(Boolean vip) {
        this.vip = vip;
    }
}
