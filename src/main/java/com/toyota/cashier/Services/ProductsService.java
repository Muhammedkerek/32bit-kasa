package com.toyota.cashier.Services;

import com.toyota.cashier.DAO.ProductsRepository;
import com.toyota.cashier.Domain.Products;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductsService {
    private final ProductsRepository productsRepository;

    public ProductsService(ProductsRepository productsRepository) {
        this.productsRepository = productsRepository;
    }
    public List<Products> getAllProducts(){
        return productsRepository.findAllActiveProducts();
    }
    public void addProduct(Products products){
        productsRepository.save(products);
    }
    // the reason for using the Optional <> Type , is that it reduces the chances of NullPointerException.
    public Optional<Products> findProductById(Long id){
        return productsRepository.findById(id);
    }
    public void deleteProductById(Long id) {
        Products product = productsRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
        product.setDeleted(true);
        productsRepository.save(product);

    }
    public Products  updateProduct(Long id , Products products){
        Products products1 = productsRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
        if(products1.isDeleted()){
            throw new RuntimeException("Cannot update a deleted product");
        }
        products1.setName(products.getName());
        products1.setPrice(products.getPrice());
        products1.setQuantity(products.getQuantity());
      return   productsRepository.save(products1);

    }

}
