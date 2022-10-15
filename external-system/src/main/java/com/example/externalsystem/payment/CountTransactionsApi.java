package com.example.externalsystem.payment;

import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
class CountTransactionsApi {

    private final Transactions transactions;

    @GetMapping("/v1/payment/accounts/{accountId}/transactions/count")
    Response countTransactions(
        @PathVariable UUID accountId,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime startDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime endDate
    ) {
        return new Response(
            accountId,
            transactions.countAllByMerchantAccountIdAndTransactionUpdateDateBetween(
                accountId,
                startDate,
                endDate
            )
        );
    }

    @AllArgsConstructor
    static class Response {
        public UUID accountId;
        public long totalItems;
    }
}
