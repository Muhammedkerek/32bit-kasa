package com.toyota.cashier.DAO;

import com.toyota.cashier.Domain.Products;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductsRepository extends JpaRepository<Products , Long> {
}
