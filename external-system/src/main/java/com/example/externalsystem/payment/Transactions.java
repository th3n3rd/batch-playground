package com.example.externalsystem.payment;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

interface Transactions extends JpaRepository<Transaction, UUID> {
    Page<Transaction> findAllByMerchantAccountIdAndTransactionUpdateDateBetween(
        UUID accountId,
        OffsetDateTime startDate,
        OffsetDateTime endDate,
        Pageable paging
    );

    @Query(value = "select transactionAmount.currencyCode, sum(transactionAmount.value) from Transaction where merchantAccountId = ?1 group by transactionAmount.currencyCode")
    List<Object[]> calculateBalancesByMerchantAccountId(UUID accountId);
}
