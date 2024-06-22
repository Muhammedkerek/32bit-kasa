package com.toyota.cashier.Resources;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.toyota.cashier.Domain.Sales;
import com.toyota.cashier.Services.SaleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class SalesResourcesTest {
    private MockMvc mockMvc;

    @Mock
    private SaleService saleService;

    @InjectMocks
    private SalesResources salesResources;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(salesResources).build();
    }

    @Test
    @WithMockUser(authorities = {"CASHIER", "ADMIN"})
    void getAllSales_ShouldReturnAllSales() throws Exception {
        List<Sales> salesList = List.of(
                new Sales(),
                new Sales()
        );

        // Stubbing the service method call
        when(saleService.getAllSales()).thenReturn(salesList);

        mockMvc.perform(get("/sales"));
    }


    @Test
    @WithMockUser(authorities = {"CASHIER", "ADMIN"})
    void getSaleById_ShouldReturnSaleById() throws Exception {
        Sales sale = new Sales();

        mockMvc.perform(get("/sales/1"))
                .andExpect(status().isOk());
    }


    @Test
    @WithMockUser(authorities = {"CASHIER", "ADMIN"})
    void createSale_ShouldCreateSale() throws Exception {
        List<Long> productIds = List.of(1L, 2L);

        when(saleService.createSale(productIds)).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(post("/create_sale")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(productIds)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(authorities = {"CASHIER", "ADMIN"})
    void updateSale_ShouldUpdateSale() throws Exception {
        List<Long> newProductIds = List.of(3L, 4L);

        when(saleService.updateSale(1L, newProductIds)).thenReturn(ResponseEntity.ok("Sale updated successfully."));

        mockMvc.perform(put("/update_sale/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(newProductIds)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(authorities = {"CASHIER", "ADMIN"})
    void deleteSale_ShouldDeleteSale() throws Exception {
        when(saleService.deleteSale(1L)).thenReturn(ResponseEntity.ok("Sale deleted successfully."));

        mockMvc.perform(delete("/sales/1"))
                .andExpect(status().isOk());
    }
}
