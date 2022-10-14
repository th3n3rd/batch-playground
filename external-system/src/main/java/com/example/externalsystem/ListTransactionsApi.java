package com.example.externalsystem;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
class ListTransactionsApi {

    private final Transactions transactions;

    @GetMapping("/v1/accounts/{accountId}/transactions")
    Response listTransactions(
        @PathVariable UUID accountId,
        @RequestParam(defaultValue = "1", required = false) int page,
        @RequestParam(defaultValue = "20", required = false) int pageSize
    ) {
        var pagedTransactions = transactions.findAllByMerchantAccountId(
            accountId,
            PageRequest.of(page, pageSize)
        );
        return new Response(
            accountId,
            pagedTransactions.getNumber(),
            pagedTransactions.getTotalElements(),
            pagedTransactions.getTotalPages(),
            pagedTransactions
                .getContent()
                .stream()
                .map(Response.Detail::new)
                .collect(Collectors.toList())
        );
    }

    @AllArgsConstructor
    static class Response {
        public UUID accountId;
        public int page;
        public long totalItems;
        public int totalPages;
        public List<Detail> transactionDetails;

        @AllArgsConstructor
        static class Detail {
            public Transaction transactionInfo;
        }
    }
}
