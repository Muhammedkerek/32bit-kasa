package com.toyota.cashier.Resources;

import com.toyota.cashier.DTO.ProductsDto;
import com.toyota.cashier.Services.ProductsService;
import jakarta.annotation.Resources;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ProductsResource {
    private final ProductsService productsService;

    public ProductsResource(ProductsService productsService) {
        this.productsService = productsService;
    }

    @GetMapping("/products")
    public List<ProductsDto> getAllProducts(){
        return productsService.listAllProducts();

    }

}
