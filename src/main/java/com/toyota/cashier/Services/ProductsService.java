package com.toyota.cashier.Services;

import com.toyota.cashier.DAO.ProductRepository;
import com.toyota.cashier.DTO.ProductsDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductsService {
    private final ProductRepository productRepository;

    public ProductsService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<ProductsDto> listAllProducts(){
        return productRepository.findAll();
    }
}
