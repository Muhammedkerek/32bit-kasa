package com.toyota.cashier.DAO;

import com.toyota.cashier.Domain.Products;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductsRepository extends JpaRepository<Products , Long> {

    // making sure to only get the products that are active (Not Soft Deleted)
    @Query("SELECT p FROM Products p WHERE p.deleted = false")
    List<Products> findAllActiveProducts();
}
