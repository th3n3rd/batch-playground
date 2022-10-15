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
class MerchantAccountDetailApi {

    private final MerchantAccounts accounts;

    @GetMapping("/v1/payment/accounts/{accountId}")
    Response countTransactions(@PathVariable UUID accountId) {
        return new Response(accounts.findById(accountId).orElseThrow());
    }

    @AllArgsConstructor
    static class Response {
        public MerchantAccount accountInfo;
    }
}
