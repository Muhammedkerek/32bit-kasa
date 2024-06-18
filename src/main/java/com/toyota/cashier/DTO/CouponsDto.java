package com.toyota.cashier.DTO;

import java.time.LocalDateTime;

public class CouponsDto {
    private Long id;
    private Double discountValue;
    private enum DiscountType{
        PERCENTAGE,
        FIXED
    }
    private DiscountType discountType;

    private LocalDateTime expirationDate;

}
