package com.toyota.cashier.Services;

import com.toyota.cashier.DAO.ProductsRepository;
import com.toyota.cashier.Domain.Products;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductsService {
    private final ProductsRepository productsRepository;

    public ProductsService(ProductsRepository productsRepository) {
        this.productsRepository = productsRepository;
    }
    public List<Products> getAllProducts(){
        return productsRepository.findAll();
    }
    public void addProduct(Products products){
        productsRepository.save(products);
    }

}
