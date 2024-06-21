package com.toyota.cashier.Services;

import com.toyota.cashier.DAO.ProductsRepository;
import com.toyota.cashier.Domain.Products;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class ProductsServiceTest {

    @Mock
    private ProductsRepository productsRepository;

    @InjectMocks
    private ProductsService productsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllProducts_ReturnsAllActiveProducts() {
        // Given
        Products product1 = new Products();
        Products product2 = new Products();
        when(productsRepository.findAllActiveProducts()).thenReturn(List.of(product1, product2));

        // When
        List<Products> allProducts = productsService.getAllProducts();

        // Then
        assertThat(allProducts).isNotNull();
        assertThat(allProducts.size()).isEqualTo(2);
        assertThat(allProducts).contains(product1, product2);
    }

    @Test
    void addProduct_SavesProduct() {
        // Given
        Products newProduct = new Products();

        // When
        productsService.addProduct(newProduct);

        // Then
        verify(productsRepository, times(1)).save(newProduct);
    }

    @Test
    void findProductById_ProductExists_ReturnsProduct() {
        // Given
        Long productId = 1L;
        Products existingProduct = new Products();
        when(productsRepository.findById(productId)).thenReturn(Optional.of(existingProduct));

        // When
        Optional<Products> foundProduct = productsService.findProductById(productId);

        // Then
        assertThat(foundProduct).isPresent();
        assertThat(foundProduct.get()).isEqualTo(existingProduct);
    }

    @Test
    void findProductById_ProductDoesNotExist_ReturnsEmptyOptional() {
        // Given
        Long productId = 1L;
        when(productsRepository.findById(productId)).thenReturn(Optional.empty());

        // When
        Optional<Products> foundProduct = productsService.findProductById(productId);

        // Then
        assertThat(foundProduct).isEmpty();
    }

    @Test
    void deleteProductById_ProductExists_DeletesProduct() {
        // Given
        Long productId = 1L;
        Products existingProduct = new Products();
        when(productsRepository.findById(productId)).thenReturn(Optional.of(existingProduct));

        // When
        productsService.deleteProductById(productId);

        // Then
        assertThat(existingProduct.isDeleted()).isTrue();
        verify(productsRepository, times(1)).save(existingProduct);
    }

    @Test
    void updateProduct_ProductExists_UpdatesProduct() {
        // Given
        Long productId = 1L;
        Products existingProduct = new Products();
        existingProduct.setName("Updated Product");
        existingProduct.setPrice(20.0);
        existingProduct.setQuantity(5L);
        Products updatedProduct = new Products();

        when(productsRepository.findById(productId)).thenReturn(Optional.of(existingProduct));
        when(productsRepository.save(existingProduct)).thenReturn(existingProduct);

        // When
        Products result = productsService.updateProduct(productId, updatedProduct);

        // Then
        assertThat(result).isEqualTo(existingProduct);
        assertThat(existingProduct.getName()).isEqualTo("Updated Product");
        assertThat(existingProduct.getPrice()).isEqualTo(20.0);
        assertThat(existingProduct.getQuantity()).isEqualTo(5L);
    }

    @Test
    void updateProduct_DeletedProduct_ThrowsException() {
        // Given
        Long productId = 1L;
        Products deletedProduct = new Products();
        deletedProduct.setDeleted(true);
        Products updatedProduct = new Products();

        when(productsRepository.findById(productId)).thenReturn(Optional.of(deletedProduct));

        // When
        RuntimeException exception = org.junit.jupiter.api.Assertions.assertThrows(
                RuntimeException.class,
                () -> productsService.updateProduct(productId, updatedProduct)
        );

        // Then
        assertThat(exception.getMessage()).isEqualTo("Cannot update a deleted product");
    }
}
