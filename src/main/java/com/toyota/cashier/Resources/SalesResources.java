package com.toyota.cashier.Resources;


import com.toyota.cashier.Domain.Sales;
import com.toyota.cashier.Services.ProductsService;
import com.toyota.cashier.Services.SaleService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class SalesResources {
    private final SaleService saleService;


    public SalesResources(SaleService saleService) {
        this.saleService = saleService;

    }

    @PreAuthorize("hasAnyAuthority('CASHIER', 'ADMIN')")
    @GetMapping("/sales")
    public List<Sales> getAllSales(){
        return saleService.getAllSales();
    }
    @PreAuthorize("hasAnyAuthority('CASHIER', 'ADMIN')")
    @GetMapping("/sales/{id}")
    public ResponseEntity<?> getSaleById(@PathVariable Long id) {
        return saleService.findSaleById(id);
    }

    @PreAuthorize("hasAnyAuthority('CASHIER', 'ADMIN')")
    @PostMapping("/create_sale")
    public ResponseEntity createSale(@RequestBody List<Long> productIds) {
        return saleService.createSale(productIds);
    }
    @PreAuthorize("hasAnyAuthority('CASHIER', 'ADMIN')")
    @PutMapping("/update_sale/{saleId}")
    public ResponseEntity<String> updateSale(@PathVariable Long saleId, @RequestBody List<Long> newProductIds) {
        return saleService.updateSale(saleId, newProductIds);
    }
    @PreAuthorize("hasAnyAuthority('CASHIER', 'ADMIN')")
    @DeleteMapping("/sales/{id}")
    public ResponseEntity<String> deleteSale(@PathVariable Long id){
        return saleService.deleteSale(id);
    }


}
