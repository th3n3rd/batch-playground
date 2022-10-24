package com.example.batch.payment;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Transactions extends JpaRepository<Transaction, UUID> {
    long countAllByAccountId(UUID accountId);
}

