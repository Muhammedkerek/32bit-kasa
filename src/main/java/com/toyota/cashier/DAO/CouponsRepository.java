package com.toyota.cashier.DAO;

import com.toyota.cashier.Domain.Coupons;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponsRepository extends JpaRepository<Coupons , Long> {
}
