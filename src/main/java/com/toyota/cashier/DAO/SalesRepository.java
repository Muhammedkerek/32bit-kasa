package com.toyota.cashier.DAO;

import com.toyota.cashier.Domain.Products;
import com.toyota.cashier.Domain.Roles;
import com.toyota.cashier.Domain.Sales;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SalesRepository extends JpaRepository<Sales, Long> {
    // // making sure to only get the Sales that are active (Not Soft Deleted)
    @Query("SELECT s FROM Sales s WHERE s.isDeleted = false")
    List<Sales> findAllActiveSales();
}
