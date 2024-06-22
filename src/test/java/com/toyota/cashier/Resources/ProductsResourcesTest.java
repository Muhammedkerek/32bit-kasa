package com.toyota.cashier.Resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.toyota.cashier.DTO.ResponseMessage;
import com.toyota.cashier.Domain.Products;
import com.toyota.cashier.Services.ProductsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;



public class ProductsResourcesTest {

    private MockMvc mockMvc;

    @Mock
    private ProductsService productsService;

    @InjectMocks
    private ProductsResources productsResources;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(productsResources).build();
    }

    @Test
    @WithMockUser(authorities = {"ADMIN", "STORE_MANAGER", "CASHIER"})
    void findProductById_ShouldReturnProduct() throws Exception {
        // Prepare a product with known details
        Products product = new Products();
        product.setId(1L);
        product.setName("Product 1");

        // Mock the service to return this product when finding by ID
        when(productsService.findProductById(1L)).thenReturn(Optional.of(product));

        mockMvc.perform(get("/products/1"));
    }

    @Test
    @WithMockUser(authorities = {"ADMIN", "STORE_MANAGER", "CASHIER"})
    void addProduct_ShouldAddProduct() throws Exception {
        // Prepare a new product object
        Products newProduct = new Products();
        newProduct.setName("New Product");

        // Mock the service to do nothing when adding a product (assuming it's a void method)
        doNothing().when(productsService).addProduct(any(Products.class));

        mockMvc.perform(post("/add_product")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(newProduct)));
    }





    @Test
    @WithMockUser(authorities = {"ADMIN", "STORE_MANAGER"})
    void deleteProductById_ShouldDeleteProduct() throws Exception {
        mockMvc.perform(delete("/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("The product was soft deleted successfully"));

        verify(productsService, times(1)).deleteProductById(1L);
    }

    @Test
    @WithMockUser(authorities = {"ADMIN", "STORE_MANAGER"})
    void updateProductById_ShouldUpdateProduct() throws Exception {
        // Prepare a product with updated details
        Products updatedProduct = new Products();
        updatedProduct.setId(1L);
        updatedProduct.setName("Updated Product");

        // Mock the service to return the updated product when updated
        when(productsService.updateProduct(eq(1L), any(Products.class))).thenReturn(updatedProduct);

        mockMvc.perform(put("/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(updatedProduct)));
    }


}
