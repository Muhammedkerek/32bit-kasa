package com.toyota.cashier.DAO;

import com.toyota.cashier.Domain.Roles;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RolesRepository extends JpaRepository<Roles, Long> {
    Optional<Roles> findByUsername(String username);
}
