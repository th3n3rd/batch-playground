package com.example.batch.scaling.none;

import com.example.batch.payment.client.MerchantAccounts;
import com.example.batch.scaling.ImportTransactionsJourneyTests;

class NoScalingJourneyTests extends ImportTransactionsJourneyTests {

    @Override
    protected void importTransactions(MerchantAccounts.Detail account) {
        client
            .post()
            .uri("/scaling/none/accounts/{accountId}/transactions", account.accountInfo.accountId)
            .exchange()
            .expectStatus().isAccepted();
    }

}
