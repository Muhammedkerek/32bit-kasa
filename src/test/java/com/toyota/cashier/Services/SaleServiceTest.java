package com.toyota.cashier.Services;

import com.toyota.cashier.DAO.CouponsRepository;
import com.toyota.cashier.DAO.ProductsRepository;
import com.toyota.cashier.DAO.SalesRepository;
import com.toyota.cashier.Domain.Coupons;
import com.toyota.cashier.Domain.Products;
import com.toyota.cashier.Domain.Sales;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class SaleServiceTest {

    @Mock
    private ProductsRepository productsRepository;

    @Mock
    private SalesRepository salesRepository;

    @Mock
    private CouponsRepository couponsRepository;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private SaleService saleService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
    }

    @Test
    void getAllSales_ReturnsAllActiveSales() {
        System.out.println("Running getAllSales_ReturnsAllActiveSales");

        Sales sale1 = new Sales();
        Sales sale2 = new Sales();
        when(salesRepository.findAllActiveSales()).thenReturn(List.of(sale1, sale2));

        List<Sales> allSales = saleService.getAllSales();

        assertThat(allSales).isNotNull();
        assertThat(allSales.size()).isEqualTo(2);
        assertThat(allSales).contains(sale1, sale2);

        System.out.println("Finished getAllSales_ReturnsAllActiveSales");
    }

    @Test
    void findSaleById_SaleExists_ReturnsSale() {
        System.out.println("Running findSaleById_SaleExists_ReturnsSale");

        Long saleId = 1L;
        Sales sale = new Sales();
        when(salesRepository.findById(saleId)).thenReturn(Optional.of(sale));

        ResponseEntity<?> response = saleService.findSaleById(saleId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(sale);

        System.out.println("Finished findSaleById_SaleExists_ReturnsSale");
    }

    @Test
    void findSaleById_SaleDoesNotExist_ReturnsNotFound() {
        System.out.println("Running findSaleById_SaleDoesNotExist_ReturnsNotFound");

        Long saleId = 1L;
        when(salesRepository.findById(saleId)).thenReturn(Optional.empty());

        ResponseEntity<?> response = saleService.findSaleById(saleId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isEqualTo("Sale not found with ID: " + saleId);

        System.out.println("Finished findSaleById_SaleDoesNotExist_ReturnsNotFound");
    }

    @Test
    void createSale_ValidProductIds_CreatesSale() {
        System.out.println("Running createSale_ValidProductIds_CreatesSale");

        List<Long> productIds = List.of(1L, 2L);

        // Initialize products with IDs
        Products product1 = new Products();
        product1.setId(1L);
        product1.setName("Product 1");
        product1.setPrice(10.0);
        product1.setQuantity(10L);

        Products product2 = new Products();
        product2.setId(2L);
        product2.setName("Product 2");
        product2.setPrice(15.0);
        product2.setQuantity(10L);

        when(authentication.getName()).thenReturn("cashier");
        when(productsRepository.findById(1L)).thenReturn(Optional.of(product1));
        when(productsRepository.findById(2L)).thenReturn(Optional.of(product2));
        when(productsRepository.save(any(Products.class))).thenReturn(product1).thenReturn(product2);

        ResponseEntity<String> response = saleService.createSale(productIds);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Sale created successfully.");
        verify(salesRepository, times(1)).save(any(Sales.class));

        System.out.println("Finished createSale_ValidProductIds_CreatesSale");
    }

    @Test
    void createSale_EmptyProductIds_ReturnsBadRequest() {
        System.out.println("Running createSale_EmptyProductIds_ReturnsBadRequest");

        List<Long> productIds = new ArrayList<>();

        ResponseEntity<String> response = saleService.createSale(productIds);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("Product IDs list cannot be empty.");

        System.out.println("Finished createSale_EmptyProductIds_ReturnsBadRequest");
    }

    @Test
    void updateSale_ValidProductIds_UpdatesSale() {
        System.out.println("Running updateSale_ValidProductIds_UpdatesSale");

        Long saleId = 1L;
        List<Long> newProductIds = List.of(1L, 2L);
        Sales existingSale = new Sales();
        existingSale.setId(saleId);
        existingSale.setProducts(new ArrayList<>());


        Products product1 = new Products();
        product1.setId(1L);
        product1.setName("Product 1");
        product1.setPrice(10.0);
        product1.setQuantity(10L);



        Products product2 = new Products();
        product2.setId(2L);
        product2.setName("Product 2");
        product2.setPrice(15.0);
        product2.setQuantity(10L);



        when(authentication.getName()).thenReturn("cashier");
        when(salesRepository.findById(saleId)).thenReturn(Optional.of(existingSale));
        when(productsRepository.findById(1L)).thenReturn(Optional.of(product1));
        when(productsRepository.findById(2L)).thenReturn(Optional.of(product2));
        when(productsRepository.save(any(Products.class))).thenReturn(product1).thenReturn(product2);
        when(salesRepository.save(any(Sales.class))).thenReturn(existingSale);

        ResponseEntity<String> response = saleService.updateSale(saleId, newProductIds);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Sale updated successfully.");

        System.out.println("Finished updateSale_ValidProductIds_UpdatesSale");
    }

    @Test
    void updateSale_EmptyProductIds_ReturnsBadRequest() {
        System.out.println("Running updateSale_EmptyProductIds_ReturnsBadRequest");

        Long saleId = 1L;
        List<Long> newProductIds = new ArrayList<>();

        ResponseEntity<String> response = saleService.updateSale(saleId, newProductIds);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("Product IDs list cannot be empty.");

        System.out.println("Finished updateSale_EmptyProductIds_ReturnsBadRequest");
    }

    @Test
    void deleteSale_SaleExists_SoftDeletesSale() {
        System.out.println("Running deleteSale_SaleExists_SoftDeletesSale");

        Long saleId = 1L;
        Sales sale = new Sales();
        sale.setId(saleId);
        when(salesRepository.findById(saleId)).thenReturn(Optional.of(sale));

        ResponseEntity<String> response = saleService.deleteSale(saleId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Sale Soft Deleted successfully.");
        assertThat(sale.getDeleted()).isTrue();
        verify(salesRepository, times(1)).save(sale);

        System.out.println("Finished deleteSale_SaleExists_SoftDeletesSale");
    }

    @Test
    void deleteSale_SaleDoesNotExist_ReturnsNotFound() {
        System.out.println("Running deleteSale_SaleDoesNotExist_ReturnsNotFound");

        Long saleId = 1L;
        when(salesRepository.findById(saleId)).thenReturn(Optional.empty());

        ResponseEntity<String> response = saleService.deleteSale(saleId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isEqualTo("Sale not found with ID: " + saleId);

        System.out.println("Finished deleteSale_SaleDoesNotExist_ReturnsNotFound");
    }
}
