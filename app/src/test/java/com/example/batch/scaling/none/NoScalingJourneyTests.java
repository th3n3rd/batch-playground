package com.example.batch.scaling.none;

import com.example.batch.payment.client.MerchantAccountDetail;
import com.example.batch.scaling.ImportTransactionsJourneyTests;
import java.time.Duration;

class NoScalingJourneyTests extends ImportTransactionsJourneyTests {

    @Override
    protected void importTransactions(MerchantAccountDetail account) {
        client
            .mutate()
            .responseTimeout(Duration.ofSeconds(300))
            .build()
            .post()
            .uri("/scaling/none/accounts/{accountId}/transactions", account.accountInfo.accountId)
            .exchange()
            .expectStatus().isAccepted();
    }

}
