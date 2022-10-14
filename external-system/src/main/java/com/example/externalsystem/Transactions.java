package com.example.externalsystem;

import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

interface Transactions extends JpaRepository<Transaction, UUID> {
    Page<Transaction> findAllByMerchantAccountId(UUID accountId, Pageable paging);
}
