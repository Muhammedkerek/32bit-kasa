package com.toyota.cashier.DAO;

import com.toyota.cashier.Domain.Sales;
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
public class SalesRepositoryTest {

    @Autowired
    private SalesRepository salesRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Sales sale1;
    private Sales sale2; // Deleted sale
    private Sales sale3;

    @BeforeEach
    public void setUp() {
        // Setting up some sales in the database for testing
        sale1 = new Sales();
        sale1.setDeleted(false);

        sale2 = new Sales();
        sale2.setDeleted(true);

        sale3 = new Sales();
        sale3.setDeleted(false);

        entityManager.persistAndFlush(sale1);
        entityManager.persistAndFlush(sale2);
        entityManager.persistAndFlush(sale3);
    }

    @Test
    public void whenFindAllActiveSales_thenReturnActiveSales() {
        // when
        List<Sales> activeSales = salesRepository.findAllActiveSales();

        // then
        assertThat(activeSales).isNotNull();
        assertThat(activeSales.size()).isEqualTo(2); // Check if it returns only active sales
        assertThat(activeSales).allMatch(sale -> !sale.getDeleted());
    }
}
