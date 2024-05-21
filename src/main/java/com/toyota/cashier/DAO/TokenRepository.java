package com.toyota.cashier.DAO;

import com.toyota.cashier.Domain.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Integer> {
    @Query("""
            Select t from Token t inner join Roles a
            on t.roles.id = a.id
            Where t.roles.id = :userId and t.loggedOut=false
            """)
    List<Token> findAllUsersById(Long userId);
    Optional<Token> findByToken (String token);

}
