package com.example.externalsystem;

import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
class ListMerchantAccountsApi {

    private final MerchantAccounts accounts;

    @GetMapping("/v1/accounts")
    Response listAccounts() {
        return new Response(
            accounts.findAll()
                .stream()
                .map(Response.Detail::new)
                .collect(Collectors.toList())
        );
    }

    @AllArgsConstructor
    static class Response {
        public List<Detail> accountDetails;

        @AllArgsConstructor
        static class Detail {
            public MerchantAccount merchantInfo;
        }
    }
}
