package com.example.externalsystem.payment;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
class CalculateBalancesApi {

    private final Transactions transactions;

    @GetMapping("/v1/payment/accounts/{accountId}/balances")
    Response calculateBalances(@PathVariable UUID accountId) {
        return new Response(
            transactions.calculateBalancesByMerchantAccountId(accountId)
                .stream()
                .map(it -> new Amount((String) it[0], (Double) it[1]))
                .collect(Collectors.toList())
        );
    }

    @AllArgsConstructor
    static class Response {
        public List<Amount> balances;
    }
}
