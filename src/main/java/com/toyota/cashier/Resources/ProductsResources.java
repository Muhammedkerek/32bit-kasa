package com.toyota.cashier.Resources;

import com.toyota.cashier.DTO.ResponseMessage;
import com.toyota.cashier.Domain.Products;
import com.toyota.cashier.Services.ProductsService;
import jakarta.persistence.GeneratedValue;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class ProductsResources {
    private final ProductsService productsService;

    public ProductsResources(ProductsService productsService) {
        this.productsService = productsService;
    }

    @GetMapping("/products")
    public List<Products> findAllProducts() {
        return productsService.getAllProducts();
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'STORE_MANAGER')")
    @PostMapping("/add_product")
    public ResponseEntity<ResponseMessage> addProduct(@Valid @RequestBody Products product) {
        productsService.addProduct(product);
        return new ResponseEntity<>(new ResponseMessage("The product was added successfully"), HttpStatus.OK);
    }
    @GetMapping("/products/{id}")
    public Optional<Products> findProductById(@PathVariable Long id){
        return productsService.findProductById(id);
    }

}
