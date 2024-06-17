package com.toyota.cashier.DTO;

import com.toyota.cashier.Domain.Products;

import java.time.LocalDateTime;
import java.util.List;

public class SalesDto {
    private Long id;
    private Double totalAmount;
    private Boolean isDeleted = false;
    private LocalDateTime saleDate;
    private String cashierName;
    private List<ProductsDto> products;
}
