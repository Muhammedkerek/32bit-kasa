package com.toyota.cashier.Domain;


import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table
public class Coupons {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Double discountValue;
    public enum DiscountType{
        PERCENTAGE,
        FIXED
    }
    @Enumerated(EnumType.STRING)
    private DiscountType discountType;
    private LocalDateTime expirationDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DiscountType getDiscountType() {
        return discountType;
    }

    public void setDiscountType(DiscountType discountType) {
        this.discountType = discountType;
    }

    public Double getDiscountValue() {
        return discountValue;
    }

    public void setDiscountValue(Double discountValue) {
        this.discountValue = discountValue;
    }

    public LocalDateTime getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDateTime expirationDate) {
        this.expirationDate = expirationDate;
    }



}
