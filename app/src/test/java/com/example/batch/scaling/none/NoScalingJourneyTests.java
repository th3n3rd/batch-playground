package com.example.batch.scaling.none;

import com.example.batch.scaling.ImportTransactionsJourneyTests;

class NoScalingJourneyTests extends ImportTransactionsJourneyTests {

    @Override
    protected void importTransactions(String accountId) {
        client
            .post()
            .uri("/scaling/none/accounts/{accountId}/transactions", accountId)
            .exchange()
            .expectStatus().isAccepted();
    }

}
