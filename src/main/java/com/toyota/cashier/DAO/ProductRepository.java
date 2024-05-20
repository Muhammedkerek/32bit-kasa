package com.toyota.cashier.DAO;

import com.toyota.cashier.DTO.ProductsDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


public interface ProductRepository extends JpaRepository<ProductsDto , Long> {

}
