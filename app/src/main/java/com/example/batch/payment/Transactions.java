package com.example.batch.payment;

import java.util.UUID;
import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface Transactions extends JpaRepository<Transaction, UUID> {
    long countAllByAccountId(UUID accountId);

    @Transactional
    @Modifying
    @Query(value = "truncate table \"transaction\"", nativeQuery = true)
    void truncate();
}

