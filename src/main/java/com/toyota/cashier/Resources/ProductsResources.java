package com.toyota.cashier.Resources;

import com.toyota.cashier.Domain.Products;
import com.toyota.cashier.Services.ProductsService;
import jakarta.persistence.GeneratedValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ProductsResources {
    private final ProductsService productsService;

    public ProductsResources(ProductsService productsService) {
        this.productsService = productsService;
    }
    @GetMapping("/products")
    public List<Products> findAllProducts(){
        return productsService.getAllProducts();
    }
}
