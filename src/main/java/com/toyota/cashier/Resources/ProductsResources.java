package com.toyota.cashier.Resources;

import com.toyota.cashier.DTO.ResponseMessage;
import com.toyota.cashier.Domain.Products;
import com.toyota.cashier.Services.ProductsService;
import jakarta.persistence.GeneratedValue;
import jakarta.validation.Valid;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

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
    @PreAuthorize("hasAnyAuthority('ADMIN', 'STORE_MANAGER' , 'CASHIER')")
    @GetMapping("/products/{id}")
    public EntityModel<Products> findProductById(@PathVariable Long id){
        Optional <Products> products =productsService.findProductById(id);
        EntityModel<Products> entityModel = EntityModel.of(products.get());
        WebMvcLinkBuilder link = linkTo(methodOn(this.getClass()).findAllProducts());
        entityModel.add(link.withRel("products"));

        return entityModel;

    }
    @PreAuthorize("hasAnyAuthority('ADMIN', 'STORE_MANAGER')")
    @DeleteMapping("/products/{id}")
    public ResponseEntity<ResponseMessage> deleteProductById(@PathVariable Long id) {
        productsService.deleteProductById(id);
        return new ResponseEntity<>(new ResponseMessage("The product was soft deleted successfully"), HttpStatus.OK);
    }
    @PreAuthorize("hasAnyAuthority('ADMIN', 'STORE_MANAGER')")
    @PutMapping("/products/{id}")
    public ResponseEntity<ResponseMessage> updateProductById(@PathVariable Long id , @RequestBody @Valid Products product){
        Products updatedProduct = productsService.updateProduct(id , product);
        return new ResponseEntity<>(new ResponseMessage("The product was updated successfully"), HttpStatus.OK);
    }


}
