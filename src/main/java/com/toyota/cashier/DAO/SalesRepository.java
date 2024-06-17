package com.toyota.cashier.DAO;

import com.toyota.cashier.Domain.Products;
import com.toyota.cashier.Domain.Roles;
import com.toyota.cashier.Domain.Sales;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SalesRepository extends JpaRepository<Sales, Long> {
    @Query("SELECT s FROM Sales s WHERE s.isDeleted = false")
    List<Sales> findAllActiveSales();
}
