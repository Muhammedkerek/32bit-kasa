package com.toyota.cashier.DAO;

import com.toyota.cashier.Domain.Products;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ComponentScan(basePackages = "com.toyota.cashier")
@ActiveProfiles("test")
public class ProductsRepositoryTest {

    @Autowired
    private ProductsRepository productsRepository;

    @Autowired
    private TestEntityManager entityManager;

    @BeforeEach
    public void setUp() {
        // Setting up some products in the database for testing
        Products product1 = new Products();
        product1.setName("Product 1");
        product1.setPrice(100.0);
        product1.setQuantity(5L);
        product1.setDeleted(false);

        Products product2 = new Products();
        product2.setName("Product 2");
        product2.setPrice(150.0);
        product2.setQuantity(2L);
        product2.setDeleted(true); // Deleted product

        Products product3 = new Products();
        product3.setName("Product 3");
        product3.setPrice(200.0);
        product3.setQuantity(3L);
        product3.setDeleted(false);

        entityManager.persistAndFlush(product1);
        entityManager.persistAndFlush(product2);
        entityManager.persistAndFlush(product3);
    }

    @Test
    public void whenFindAllActiveProducts_thenReturnActiveProducts() {
        // when
        List<Products> activeProducts = productsRepository.findAllActiveProducts();

        // then
        assertThat(activeProducts).isNotNull();
        assertThat(activeProducts.size()).isEqualTo(2); // Check if it returns only active products
        assertThat(activeProducts).extracting(Products::getName).contains("Product 1", "Product 3");
    }

    @Test
    public void whenFindAllActiveProducts_noDeletedProductsReturned() {
        // when
        List<Products> activeProducts = productsRepository.findAllActiveProducts();

        // then
        assertThat(activeProducts).allMatch(product -> !product.isDeleted()); // Ensure all returned products are not deleted
    }
}
